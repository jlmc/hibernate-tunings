package io.costax.hibernatetuning.softdelete;

import io.costax.rules.EntityManagerProvider;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.OffsetDateTime.parse;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SoftDeleteTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void a0_createSomeTags() {
        provider.beginTransaction();

        Tag workTag = Tag.of(1L, "Work");
        provider.em().persist(workTag);

        Tag houseTag = Tag.of(2L, "House");
        provider.em().persist(houseTag);

        Tag personalTag = Tag.of(3L, "Personal");
        provider.em().persist(personalTag);

        provider.commitTransaction();
    }

    @Test
    public void a1_createSomeTodo() {
        provider.beginTransaction();

        Todo todo1 = Todo.of(1L, "See Big Bang Theory");
        todo1.addComment(TodoComment.of(1L, "T4 E1"));
        todo1.addComment(TodoComment.of(2L, "T4 E2"));
        todo1.addComment(TodoComment.of(3L, "T4 E3"));
        todo1.addComment(TodoComment.of(4L, "T4 E4"));

        final EntityManager em = provider.em();
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

        provider.commitTransaction();
    }

    @Test
    public void a2_addDetails() {
        provider.beginTransaction();

        final Todo bigBangTheory = provider.em().find(Todo.class, 1L);
        bigBangTheory.addDetails(TodoDetails.of(parse("2018-12-01T21:23:05.125058Z"), "J.Costa"));

        final Todo goShopping = provider.em().find(Todo.class, 2L);
        goShopping.addDetails(TodoDetails.of(parse("2018-12-01T08:30:00.000000Z"), "J.Sousa"));

        provider.commitTransaction();
    }

    private OffsetDateTime toOffSetDateTime(final String txt) {
        return OffsetDateTime.parse(txt, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @Test
    public void b_findById() {
        final Todo todo = provider.em().find(Todo.class, 1L);
        Assert.assertNotNull(todo);
        Assert.assertEquals("See Big Bang Theory", todo.getTitle());

        assertThat(todo.getDetails(), notNullValue());
    }

    @Test
    public void c_loadAllTodo() {
        final List<Todo> allTodo = provider.em()
                .createQuery("select t from Todo t order by t.id", Todo.class)
                .getResultList();

        assertThat(allTodo, Matchers.hasSize(3));
    }

    @Test
    public void c1_removeOneCommentFromTodo() {
        provider.beginTransaction();
        final Todo todo = provider.em().find(Todo.class, 1L);
        final TodoComment todoComment = provider.em().find(TodoComment.class, 1L);

        Assert.assertNotNull(todo);
        Assert.assertNotNull(todoComment);
        assertThat(todoComment.getReview(), is("T4 E1"));

        todo.removeComment(todoComment);

        provider.em().flush();
        provider.commitTransaction();

        final Number count = (Number) provider.em()
                .createNativeQuery("select count(id) from tasks.todo_comment where id = 1 and todo_id = 1 and deleted = true")
                .getSingleResult();

        assertThat(count.longValue(), is(1L));
    }

    @Test
    public void c2_loadTodoWithComments() {
        final Todo todo = provider.em()
                .createQuery("select distinct t from Todo t left join fetch t.comments c where t.id = :id", Todo.class)
                .setParameter("id", 1L)
                .getSingleResult();

        assertThat(todo, notNullValue());
        assertThat(todo.getComments(), hasSize(3));
        assertThat(todo.getComments(),
                containsInAnyOrder(
                        hasProperty("id", is(2L)),
                        hasProperty("id", is(3L)),
                        hasProperty("id", is(4L))));
    }

    @Test
    public void c3_loadAllTodoFetchComments() {
        final List<Todo> allTodo = provider.em()
                .createQuery("select distinct t from Todo t left join fetch t.comments", Todo.class)
                .getResultList();

        assertThat(allTodo, Matchers.hasSize(3));
    }

    @Test
    public void d1_deleteById() {
        provider.beginTransaction();

        final Todo todo = provider.em().find(Todo.class, 1L);
        Assert.assertNotNull(todo);

        provider.em().remove(todo);

        provider.commitTransaction();

        // The soft delete configurations are applied to any select JPQL query
        final Long numberOfTodos = provider.em().createQuery("select count (t) from Todo t", Long.class).getSingleResult();

        assertThat(numberOfTodos, is(2L));
    }

    @Test
    public void d2_deleteDetails() {
        provider.beginTransaction();
        final Todo todo = provider.em().find(Todo.class, 2L);
        todo.removeDetails();
        provider.commitTransaction();
    }

    @Test(expected = javax.persistence.RollbackException.class)
    public void d3_addDetailsAgain() {
        try {
            // this use case have the limitation that we have to find a pre-existing  details
            provider.beginTransaction();
            final Todo todo = provider.em().find(Todo.class, 2L);
            todo.addDetails(TodoDetails.of(parse("2018-12-15T15:45:00.000000Z"), "J.F.Sousa"));

            provider.commitTransaction();
        } catch (Exception e) {
            System.err.println(e);
            throw e;
        }
    }

    @Test
    public void d4_addDetailsAgainUsingAPossiblePreExistingRecord() {
        // this use case have the limitation that we have to find a pre-existing  details
        provider.beginTransaction();
        final Todo todo = provider.em().find(Todo.class, 2L);

        TodoDetails details = null;
        try {
            details = (TodoDetails) provider.em()
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

        provider.commitTransaction();
    }

    @Test
    public void e1_findTodoWithTags() {
        final Todo christmasGifts = provider.em()
                .createQuery(
                        "select distinct t " +
                                "from Todo t " +
                                "left join fetch t.tags " +
                                "left join fetch t.details left join fetch t.comments " +
                                "where t.id = :id", Todo.class)
                .setParameter("id", 3L)
                .getSingleResult();

        assertThat(christmasGifts, notNullValue());
        assertThat(christmasGifts.getTags(), hasSize(3));
    }

    @Test
    public void e2_deleteOneTag() {
        provider.beginTransaction();

        // The delete is not valid to a JPQL delete
        /*
        provider.em().createQuery("delete from Tag where id = :id")
                .setParameter("id", 3L)
                .executeUpdate();
                */

        final Tag reference = provider.em().getReference(Tag.class, 3L);
        if (reference != null) {
            provider.em().remove(reference);
        }

        provider.commitTransaction();

        final List<Tag> tags = provider.em().createQuery("select t from Tag t", Tag.class).getResultList();

        assertThat(tags, hasSize(2));
        assertThat(tags, not(contains(hasProperty("id", is(3L)))));

        final Todo todo = provider.em().find(Todo.class, 3L);
        assertThat(todo.getTags(), hasSize(2));
        assertThat(todo.getTags(), not(contains(hasProperty("id", is(3L)))));
    }

    @Test
    public void e3_removeTagFromTodo() {
        provider.beginTransaction();

        final Tag work = provider.em().getReference(Tag.class, 2L);
        final Todo gifts = provider.em().find(Todo.class, 3L);
        gifts.removeTag(work);

        provider.commitTransaction();

        final Todo todo = provider.em().find(Todo.class, 3L);
        assertThat(todo.getTags(), not(contains(hasProperty("id", is(2L)))));
        assertThat(todo.getTags(), hasSize(1));
    }

    @Test
    public void z_removeAllTestData() {
        provider.beginTransaction();

        provider.em()
                .createNativeQuery("delete from tasks.todo_tag")
                .executeUpdate();

        provider.em()
                .createNativeQuery("delete from tasks.tag where id > 0")
                .executeUpdate();

        provider.em()
                .createNativeQuery("delete from tasks.todo_comment where id > 0")
                .executeUpdate();

        provider.em()
                .createNativeQuery("delete from tasks.todo_details where id > 0")
                .executeUpdate();

        provider.em()
                .createNativeQuery("delete from tasks.todo where id > 0")
                .executeUpdate();

        provider.commitTransaction();
    }
}
