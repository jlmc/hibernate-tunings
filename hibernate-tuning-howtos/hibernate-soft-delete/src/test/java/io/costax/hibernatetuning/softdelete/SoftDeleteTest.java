package io.costax.hibernatetuning.softdelete;

import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.OffsetDateTime.parse;
import static org.junit.jupiter.api.Assertions.*;

@JpaTest(persistenceUnit = "it")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class SoftDeleteTest {

    @JpaContext
    public JpaProvider provider;

    private void createSomeTags(EntityManager em) {
        Tag workTag = Tag.of(1L, "Work");
        em.persist(workTag);

        Tag houseTag = Tag.of(2L, "House");
        em.persist(houseTag);

        Tag personalTag = Tag.of(3L, "Personal");
        em.persist(personalTag);

        em.flush();
    }

    public void createSomeTodos(EntityManager em) {
        Todo todo1 = Todo.of(1L, "See Big Bang Theory");
        todo1.addComment(TodoComment.of(1L, "T4 E1"));
        todo1.addComment(TodoComment.of(2L, "T4 E2"));
        todo1.addComment(TodoComment.of(3L, "T4 E3"));
        todo1.addComment(TodoComment.of(4L, "T4 E4"));

        em.persist(todo1);

        Todo todo2 = Todo.of(2L, "Go shopping");
        todo2.addComment(TodoComment.of(5L, "Rice"));
        todo2.addComment(TodoComment.of(6L, "Milk"));
        todo2.addComment(TodoComment.of(7L, "Bread"));
        em.persist(todo2);

        Todo todo3 = Todo.of(3L, "Christmas gifts");
        todo3.addComment(TodoComment.of(8L, "J.Sousa"));
        todo3.addComment(TodoComment.of(9L, "Parents"));

        todo3.addTag(em.getReference(Tag.class, 1L));
        todo3.addTag(em.getReference(Tag.class, 2L));
        todo3.addTag(em.getReference(Tag.class, 3L));

        em.persist(todo3);

        em.flush();
    }

    @SuppressWarnings("SqlDialectInspection")
    public void removeAllData(EntityManager em) {
        em.createNativeQuery("delete from t_todo_tag").executeUpdate();
        em.createNativeQuery("delete from t_tag where id > 0").executeUpdate();
        em.createNativeQuery("delete from t_todo_comment where id > 0").executeUpdate();
        em.createNativeQuery("delete from t_todo_details where id > 0").executeUpdate();
        em.createNativeQuery("delete from t_todo where id > 0").executeUpdate();
        em.flush();
    }

    public void addTodoDetails(EntityManager em) {
        Todo bigBangTheory = em.find(Todo.class, 1L);
        bigBangTheory.addDetails(
                TodoDetails.of(parse("2018-12-01T21:23:05.125058Z"),
                        "J.Costa"));

        Todo goShopping = em.find(Todo.class, 2L);
        goShopping.addDetails(
                TodoDetails.of(parse("2018-12-01T08:30:00.000000Z"),
                        "J.Sousa"));

        em.flush();
    }

    @BeforeEach
    public void prepareTheAllThing() {
        provider.doInTx(em -> {
            System.out.println("---- Delete All data");
            removeAllData(em);
            System.out.println("---- Create Some Tags");
            createSomeTags(em);
            System.out.println("---- Create Some Todos");
            createSomeTodos(em);
            System.out.println("---- Add Todo Details Some Todos");
            addTodoDetails(em);
        });
    }


    @Test
    @Order(0)
    public void softDeleteTest() {
        readTodos();

        System.out.println("Try to remove comment from todo");
        removeOneCommentFromTodo();


        System.out.println("Try fetch one Todo with it Comments");
        fetchOneTodoWithItsComments();

        loadAllTodoFetchingItsComments();

        deleteOneTodoById();

        deleteOneTodoDetails();

        addTodoDetailsWhenExistData();

        System.out.println("---");
    }

    private void readTodos() {
        EntityManager em = provider.em();

        //
        findTodoById(em);

        findAllTodos(em);
    }

    private void findTodoById(EntityManager em) {
        System.out.println("--- Find by Id");

        Todo todo = em.find(Todo.class, 1L);
        assertNotNull(todo);
        assertEquals("See Big Bang Theory", todo.getTitle());
        assertNotNull(todo.getDetails());
    }


    public void findAllTodos(EntityManager em) {
        System.out.println("--- Find All Todo");
        List<Todo> resultList =
                em.createQuery("select distinct t from Todo t left join fetch t.details order by t.id", Todo.class)
                  .getResultList();
        assertEquals(3, resultList.size());
    }


    /**
     * when remove one comment it should execute an Update statement instead of a delete.
     */
    public void removeOneCommentFromTodo() {
        System.out.println("---when remove one comment it should execute an Update statement instead of a delete.");

        provider.doInTx(em -> {

            final Todo todo = em.find(Todo.class, 1L);
            final TodoComment todoComment = em.find(TodoComment.class, 1L);

            assertNotNull(todo);
            assertNotNull(todoComment);

            todo.removeComment(todoComment);

            em.flush();
        });

        final Number count = provider.doItWithReturn(em -> {

            return (Number) em
                    .createNativeQuery("""
                            select count(id) 
                            from t_todo_comment 
                            where id = 1 
                            and todo_id = 1 
                            and deleted = true
                            """)
                    .getSingleResult();
        });

        assertEquals(1L, count.longValue());
    }

    public void fetchOneTodoWithItsComments() {
        Todo todo = provider.em()
                            .createQuery("""
                                    select distinct t
                                    from Todo t
                                    left join fetch t.details
                                    left join fetch t.comments c
                                    where t.id = :id
                                    """, Todo.class)
                            .setParameter("id", 1L)
                            .getSingleResult();

        assertNotNull(todo);
        assertNotNull(todo.getComments());
        assertEquals(3, todo.getComments().size());
        assertTrue(() -> todo.getComments()
                             .stream()
                             .map(TodoComment::getId)
                             .collect(Collectors.toSet())
                             .containsAll(Set.of(2L, 3L, 4L)));
    }


    public void loadAllTodoFetchingItsComments() {
        final List<Todo> allTodos =
                provider.em()
                        .createQuery("""
                                select distinct t
                                from Todo t
                                left join fetch t.details
                                left join fetch t.comments
                                """, Todo.class)
                        .getResultList();

        assertEquals(3, allTodos.size());
    }


    public void deleteOneTodoById() {
        EntityManager em = provider.em();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        final Todo todo = em.find(Todo.class, 1L);
        em.remove(todo);

        em.flush();
        tx.commit();

        // The soft delete configurations are applied to any select JPQL query
        Long numberOfTodos =
                em.createQuery("select count (t) from Todo t", Long.class)
                  .getSingleResult();

        assertEquals(2, numberOfTodos);
    }

    public void deleteOneTodoDetails() {
        EntityManager em = provider.em();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        final Todo todo = em.find(Todo.class, 2L);
        todo.removeDetails();

        em.flush();
        tx.commit();
    }


    public void addTodoDetailsWhenExistData() {
        EntityManager em = provider.em();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            //Todo todo = em.find(Todo.class, 2L);
            Todo todo =
                    em.createQuery("select t from Todo t left join  fetch t.details where t.id = :id", Todo.class)
                      .setParameter("id", 2L)
                      .getSingleResult();

            todo.addDetails(
                    TodoDetails.of(parse("2018-12-15T15:45:00.000000Z"),
                            "J.F.Sousa"));

            em.flush();
            tx.commit();

            Assertions.fail();
        } catch (RuntimeException e) {
            tx.rollback();

            System.out.println("Expected Error: " + e.getMessage());
        }
    }
}
