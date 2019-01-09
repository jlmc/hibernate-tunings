package io.costax.hibernatetunning.paginantion;

import io.costax.hibernatetunning.tasks.DistinctDetachTodoResultTransformer;
import io.costax.hibernatetunning.tasks.Todo;
import io.costax.hibernatetunning.tasks.TodoComment;
import io.costax.rules.EntityManagerProvider;
import org.hibernate.HibernateException;
import org.hibernate.query.NativeQuery;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaginationOneToManyWithLazyFieldTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void a_populate() {

        long commentsSeq = 1L;

        final EntityManager em = provider.em();
        provider.beginTransaction();

        for (int i = 1; i < 50; i++) {
            final Todo td = Todo.of((long) i, "todo-" + i);

            if (i == 1) {
                for (int y = 1; y < 4; y++) {
                    final String review = "todo-comment-" + td.getId() + "--" + y;
                    final TodoComment tc = TodoComment.of((commentsSeq++), review);
                    tc.setAttachment(review.getBytes());
                    td.addComment(tc);
                }
            }

            if (i == 3) {
                for (int y = 1; y < 2; y++) {
                    final String review = "todo-comment-" + td.getId() + "--" + y;
                    final TodoComment tc = TodoComment.of((commentsSeq++), review);
                    tc.setAttachment(review.getBytes());
                    td.addComment(tc);
                }
            }

            if (i == 5) {
                for (int y = 1; y < 3; y++) {
                    final TodoComment tc = TodoComment.of((commentsSeq++), "todo-comment-" + td.getId() + "--" + y);
                    // tc.setAttachment(review.getBytes());
                    td.addComment(tc);
                }
            }

            if (i == 13) {
                for (int y = 1; y < 7; y++) {
                    final TodoComment tc = TodoComment.of((commentsSeq++), "todo-comment-" + td.getId() + "--" + y);
                    td.addComment(tc);
                }
            }

            em.persist(td);
        }

        provider.commitTransaction();
    }

    @Test
    public void b_should_fetch_commentAttachment_in_using_a_extra_query() {
        final Todo todo = provider.em().createQuery("select distinct td from Todo td left join fetch td.comments where td.id = :id", Todo.class)
                .setParameter("id", 1L)
                .getSingleResult();

        assertThat(todo, notNullValue());
        assertThat(todo.getComments(), hasSize(3));

        final TodoComment todoComment1 = todo.getComments().stream().filter(todoComment -> todoComment.getId() == 1L).findFirst().orElse(null);
        assertThat(todoComment1, notNullValue());
        final byte[] attachment = todoComment1.getAttachment();
        assertThat(new String(attachment, StandardCharsets.UTF_8), is("todo-comment-1--1"));
    }

    @Test
    public void c_should_fetchTestPage1() {
        provider.beginTransaction();

        int page = 0;
        int pageSize = 5;

        List<Todo> todos = getTodosPage(page, pageSize);

        assertThat(todos, hasSize(pageSize));

        final Todo first = todos.get(0);
        assertThat(first, notNullValue());
        final Todo second = todos.get(1);
        assertThat(second, notNullValue());
        final Todo third = todos.get(2);
        assertThat(third, notNullValue());
        final Todo fourth = todos.get(3);
        assertThat(fourth, notNullValue());
        final Todo fifth = todos.get(4);
        assertThat(fifth, notNullValue());

        assertThat(first.getId(), is(1L));
        assertThat(first.getTitle(), is("todo-1"));
        assertThat(first.getComments(), hasSize(3));
        assertThat(first.getComments(), hasItems(
                hasProperty("id", is(2L)),
                hasProperty("id", is(3L)),
                hasProperty("id", is(1L))));

        assertThat(second.getId(), is(10L));
        assertThat(second.getTitle(), is("todo-10"));
        assertThat(second.getComments(), is(empty()));

        assertThat(third.getId(), is(11L));
        assertThat(third.getTitle(), is("todo-11"));
        assertThat(third.getComments(), is(empty()));

        assertThat(fourth.getId(), is(12L));
        assertThat(fourth.getTitle(), is("todo-12"));
        assertThat(fourth.getComments(), is(empty()));

        assertThat(fifth.getId(), is(13L));
        assertThat(fifth.getTitle(), is("todo-13"));
        assertThat(fifth.getComments(), hasSize(6));
        assertThat(fifth.getComments(), hasItems(
                hasProperty("id", is(12L)),
                hasProperty("id", is(7L)),
                hasProperty("id", is(8L)),
                hasProperty("id", is(9L)),
                hasProperty("id", is(10L)),
                hasProperty("id", is(11L))));

        // now execute one more query to get the attachment

        final TodoComment todoComment1 = first.getComments().stream().filter(todoComment -> todoComment.getId() == 1L).findFirst().orElse(null);
        assertThat(todoComment1, notNullValue());

        provider.commitTransaction();
    }

    @Test
    public void c_should_fetchTestPage2() {
        int page = 2;
        int pageSize = 5;

        List<Todo> todos = getTodosPage(page, pageSize);

        assertThat(todos, hasSize(pageSize));

        assertThat(todos, contains(
                hasProperty("id", is(19L)),
                hasProperty("id", is(2L)),
                hasProperty("id", is(20L)),
                hasProperty("id", is(21L)),
                hasProperty("id", is(22L))
        ));
    }

    private List<Todo> getTodosPage(final int page, final int pageSize) {
        final int firstRecord = page * pageSize + 1;
        final int lastRecord = firstRecord + pageSize - 1;

        final EntityManager em = provider.em();

        List<Todo> todos = em.createNamedQuery("TodoWithCommentByRank")
                .setParameter("firstRecord", firstRecord)
                .setParameter("lastRecord", lastRecord)
                //.setHint(QueryHints.HINT_READONLY, "true")
                .unwrap(NativeQuery.class)
                //.addEntity( "td", Todo.class )
                //.addEntity( "tdc", TodoComment.class )
                .setResultTransformer(new DistinctDetachTodoResultTransformer(em))
                .getResultList();

        return todos;
    }

    @Test
    public void d_should_throw_exception_when_load_fields_lazy() {
        provider.beginTransaction();

        int page = 0;
        int pageSize = 5;

        List<Todo> todos = getTodosPage(page, pageSize);

        final Todo todo1 = todos.stream().filter(td -> td.getId() == 1L).findFirst().orElse(null);
        assertThat(todo1, notNullValue());

        final TodoComment todoComment = todo1.getComments().stream().filter(tdc -> tdc.getId() == 2L).findFirst().orElse(null);
        assertThat(todoComment, notNullValue());

        try {
            todoComment.getAttachment();

            Assert.fail("should failed...");
        } catch (HibernateException e) {
            Assert.assertThat(e.getMessage(), is("entity is not associated with the session: null"));

            // org.hibernate.HibernateException: entity is not associated with the session: null
        }

        provider.commitTransaction();
    }

    @Test
    public void e_should_fetch_and_paginate_using_denseRank_and_merge() {
        int page = 0;
        int pageSize = 5;

        List<Todo> todos = getTodosPage(page, pageSize);

        final Todo todo1 = todos.stream().filter(td -> td.getId() == 1L).findFirst().orElse(null);
        assertThat(todo1, notNullValue());
        assertThat(todo1.getComments(), hasSize(3));
        final TodoComment todoComment1 = todo1.getComments().stream().filter(tdc -> tdc.getId() == 1L).findFirst().orElse(null);
        assertThat(todoComment1, notNullValue());
        todo1.removeComment(todoComment1);


        final Todo todo10 = todos.stream().filter(td -> td.getId() == 10L).findFirst().orElse(null);
        assertThat(todo10, notNullValue());

        todo10.addComment(TodoComment.of(598L, "Awesome!"));

        provider.beginTransaction();

        provider.em().merge(todo1);
        provider.em().merge(todo10);

        provider.commitTransaction();
    }


    @Test
    public void z_remove_all_data() {
        provider.beginTransaction();
        provider.em().createQuery("delete from TodoComment ").executeUpdate();
        provider.em().createQuery("delete from Todo ").executeUpdate();
        provider.commitTransaction();
    }

    /*
    public static class DistinctTodoResultTransformer extends BasicTransformerAdapter {
        static final ResultTransformer INSTANCE = new DistinctTodoResultTransformer();

        @Override
        public List transformList(List list) {
            Map<Serializable, Object> identifiableMap = new LinkedHashMap<>(list.size());

            for (Object entityArray : list) {
                if (Object[].class.isAssignableFrom(entityArray.getClass())) {
                    Todo todo = null;
                    TodoComment todoComment = null;

                    Object[] tuples = (Object[]) entityArray;

                    for (Object tuple : tuples) {

                        if (tuple instanceof Todo) {
                            todo = (Todo) tuple;
                        } else if (tuple instanceof TodoComment) {
                            todoComment = (TodoComment) tuple;
                        } else if (tuple != null) {
                            // because some TODOS may have no comments
                            throw new UnsupportedOperationException("Tuple " + tuple.getClass() + " is not supported!");
                        }
                    }

                    Objects.requireNonNull(todo);

                    if (!identifiableMap.containsKey(todo.getId())) {
                        identifiableMap.put(todo.getId(), todo);

                        // this define is very important to prevent the N + 1 query problems
                        todo.setComments(new ArrayList<>());
                    }

                    if (todoComment != null) {
                        todo.addComment(todoComment);
                    }
                }
            }
            return new ArrayList<>(identifiableMap.values());
        }
    }
    */
}
