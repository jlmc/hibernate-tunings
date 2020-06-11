package io.costax.hibernatetuning.softdelete;

import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Objects;
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

    @Test
    @Order(0)
    public void create_some_tags() {
        provider.doInTx(em -> {
            Tag workTag = Tag.of(1L, "Work");
            em.persist(workTag);

            Tag houseTag = Tag.of(2L, "House");
            em.persist(houseTag);

            Tag personalTag = Tag.of(3L, "Personal");
            em.persist(personalTag);
        });
    }

    @Test
    @Order(1)
    public void create_some_todo() {
        provider.doInTx(em -> {

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
        });
    }

    @Test
    @Order(2)
    public void add_details() {
        provider.doInTx(em -> {
            final Todo bigBangTheory = em.find(Todo.class, 1L);
            bigBangTheory.addDetails(TodoDetails.of(parse("2018-12-01T21:23:05.125058Z"), "J.Costa"));

            final Todo goShopping = em.find(Todo.class, 2L);
            goShopping.addDetails(TodoDetails.of(parse("2018-12-01T08:30:00.000000Z"), "J.Sousa"));
        });
    }

    @Test
    @Order(3)
    public void find_todo_by_id() {
        final EntityManager em = provider.em();

        final Todo todo = em.find(Todo.class, 1L);

        assertNotNull(todo);
        assertEquals("See Big Bang Theory", todo.getTitle());
        assertNotNull(todo.getDetails());

        em.close();
    }

    @Test
    @Order(4)
    public void load_all_todos_entities() {
        final List<Todo> allTodo = provider.doItWithReturn(em ->
                em.createQuery("select t from Todo t order by t.id", Todo.class)
                        .getResultList());

        assertEquals(3, allTodo.size());
    }

    @Test
    @Order(5)
    public void remove_one_comment_from_todo_entity() {
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
                            from tasks.todo_comment 
                            where id = 1 
                            and todo_id = 1 
                            and deleted = true
                            """)
                    .getSingleResult();
        });

        assertEquals(1L, count.longValue());
    }

    @Test
    @Order(6)
    public void load_todo_with_comments() {
        final Todo todo = provider.doItWithReturn(em -> em
                .createQuery("select distinct t from Todo t left join fetch t.comments c where t.id = :id", Todo.class)
                .setParameter("id", 1L)
                .getSingleResult());

        assertNotNull(todo);
        assertNotNull(todo.getComments());
        assertEquals(3, todo.getComments().size());

        assertTrue(() -> todo.getComments()
                .stream()
                .map(TodoComment::getId)
                .collect(Collectors.toSet())
                .containsAll(Set.of(2L, 3L, 4L)));
    }

    @Test
    @Order(7)
    public void load_all_todo_fetching_its_comments() {
        final List<Todo> allTodos = provider.doItWithReturn(em ->
                em.createQuery("select distinct t from Todo t left join fetch t.comments", Todo.class)
                        .getResultList());

        assertEquals(3, allTodos.size());
    }

    @Test
    @Order(8)
    public void delete_todo_entity_by_id() {
        provider.doInTx(em -> {

            final Todo todo = em.find(Todo.class, 1L);
            em.remove(todo);

        });

        final Long numberOfTodos = provider.doItWithReturn(em ->
                em.createQuery("select count (t) from Todo t", Long.class).getSingleResult());

        // The soft delete configurations are applied to any select JPQL query

        assertEquals(2, numberOfTodos);
    }

    @Test
    @Order(9)
    public void delete_details_entities() {

        provider.doInTx(em -> {
            final Todo todo = em.find(Todo.class, 2L);
            todo.removeDetails();
        });

    }

    @Test
    @Order(10)
    public void add_details_entities_again() {
        final Exception rollbackException = assertThrows(Exception.class, () -> {

            provider.doInTx(em -> {
                try {
                    // this use case have the limitation that we have to find a pre-existing  details
                    final Todo todo = em.find(Todo.class, 2L);
                    todo.addDetails(TodoDetails.of(parse("2018-12-15T15:45:00.000000Z"), "J.F.Sousa"));

                } catch (Exception e) {
                    System.err.println(e);
                    throw e;
                }
            });
        });

        assertNotNull(rollbackException.getCause());
        assertTrue(rollbackException.getCause() instanceof ConstraintViolationException);
        ConstraintViolationException constraintViolationException = (ConstraintViolationException) rollbackException.getCause();
        assertEquals("todo_details_pkey", constraintViolationException.getConstraintName());
        assertEquals("insert into tasks.todo_details (deleted, created_by, created_on, id) values (?, ?, ?, ?)", constraintViolationException.getSQL());
        String expectedSqlErrorMessage = "insert into tasks.todo_details (deleted, created_by, created_on, id) values ('FALSE', 'J.F.Sousa', '2018-12-15 15:45:00+00'::timestamp with time zone, 2) was aborted: ERROR: duplicate key value violates unique constraint";
        assertTrue(constraintViolationException.getSQLException().getMessage().contains(expectedSqlErrorMessage));
    }

    @Test
    @Order(11)
    public void add_details_again_using_a_possible_pre_existing_record() {
        // this use case have the limitation that we have to find a pre-existing  details
        provider.doInTx(em -> {

            final Todo todo = em.find(Todo.class, 2L);

            TodoDetails details = null;
            try {
                details = (TodoDetails) em
                        .createNativeQuery("select * from tasks.todo_details d where d.id = :todoId and d.deleted = true or d.deleted = false", TodoDetails.class)
                        .setParameter("todoId", todo.getId())
                        .getSingleResult();

            } catch (NoResultException e) {
                // nothing
            }

            if (details == null) {
                todo.addDetails(TodoDetails.of(parse("2018-12-15T15:45:00.000000Z"), "J.F.Sousa"));
            } else {

                // unfortu
                details.setCreatedBy("J.Filipa.Sousa");
                details.setCreatedOn(parse("2018-12-15T15:45:00.000000Z"));
                details.setDelete(false);

                todo.addDetails(details);
            }

        });
    }

    @Test
    @Order(12)
    public void find_todo_with_tags() {
        final Todo christmasGifts = provider.doItWithReturn(em ->
                em.createQuery(
                        "select distinct t " +
                                "from Todo t " +
                                "left join fetch t.tags " +
                                "left join fetch t.details left join fetch t.comments " +
                                "where t.id = :id", Todo.class)
                        .setParameter("id", 3L)
                        .getSingleResult());

        Assertions.assertNotNull(christmasGifts);
        Assertions.assertEquals(3, christmasGifts.getTags().size());
    }

    @Test
    @Order(13)
    public void delete_one_tag() {
        provider.doInTx(em -> {
            // The delete is not valid to a JPQL delete

            final Tag reference = em.getReference(Tag.class, 3L);
            if (reference != null) {
                em.remove(reference);
            }

        });

        provider.doIt(em -> {
            final List<Tag> tags = em.createQuery("select t from Tag t", Tag.class).getResultList();
            final Number countTagsRecords = (Number) em.createNativeQuery("select count(*) from tasks.Tag t").getSingleResult();

            Assertions.assertEquals(2, tags.size());
            Assertions.assertEquals(3L, countTagsRecords.longValue());

            Assertions.assertFalse(() -> tags.stream().map(Tag::getId).anyMatch(id -> Objects.equals(3L, id)));

        });

    }

    @Test
    @Order(14)
    public void remove_tag_from_todo() {
        provider.doInTx(em -> {
            final Tag work = em.getReference(Tag.class, 2L);
            final Todo gifts = em.find(Todo.class, 3L);
            gifts.removeTag(work);

        });

        provider.doIt(em -> {

            final Todo todo = em.find(Todo.class, 3L);

            final Set<Long> tagsIds = todo.getTags().stream().map(Tag::getId).collect(Collectors.toSet());
            Assertions.assertFalse(tagsIds.contains(2L));
            assertTrue(tagsIds.size() == 1);
        });

    }

    @Test
    @Order(14)
    public void remove_all_test_data() {
        provider.doInTx(em -> {
            em.createNativeQuery("delete from tasks.todo_tag").executeUpdate();
            em.createNativeQuery("delete from tasks.tag where id > 0").executeUpdate();
            em.createNativeQuery("delete from tasks.todo_comment where id > 0").executeUpdate();
            em.createNativeQuery("delete from tasks.todo_details where id > 0").executeUpdate();
            em.createNativeQuery("delete from tasks.todo where id > 0").executeUpdate();
        });
    }
}
