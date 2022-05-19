package io.costax.hibernatetunning.paginantion.hibernate6;

import io.costax.hibernatetunning.tasks.DistinctDetachTodoResultTransformer;
import io.costax.hibernatetunning.tasks.Todo;
import io.costax.hibernatetunning.tasks.TodoComment;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.annotation.Sql;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.HibernateException;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test to run successfully in the IDE is necessary to disable automatic compilation in the run memento,
 * using only the bytecode resulting from the maven build!
 * <p>
 * This is because the model classes must be targeted by the maven plugin
 * {@code org.hibernate.orm.tooling:hibernate-enhance-maven-plugin} enableLazyInitialization.
 * </p>
 */
@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql(statements = {
        "delete from tasks.todo_comment where true",
        "delete from tasks.todo where true",
}, phase = Sql.Phase.AFTER_TEST_METHOD)
public class PaginationOneToManyWithLazyFieldTest {

    @JpaContext
    JpaProvider provider;

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    public void populate() {
        provider.doInTx(em -> {

        long commentsSeq = 1L;

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
        });

    }

    @Test
    @Order(1)
    public void should_fetch_commentAttachment_in_using_a_extra_query() {
        final Todo todo = em.createQuery("select distinct td from Todo td left join fetch td.comments where td.id = :id", Todo.class)
                .setParameter("id", 1L)
                .getSingleResult();

        assertNotNull(todo);
        assertEquals(3, todo.getComments().size());

        final TodoComment todoComment1 = todo.getComments().stream().filter(todoComment -> todoComment.getId() == 1L).findFirst().orElse(null);
        assertNotNull(todoComment1);
        final byte[] attachment = todoComment1.getAttachment();
        assertEquals("todo-comment-1--1", new String(attachment, StandardCharsets.UTF_8));
    }

    @Test
    @Order(1)
    public void should_fetch_test_page1() {
       em.getTransaction().begin();

        int page = 0;
        int pageSize = 5;

        List<Todo> todos = getTodosPage(page, pageSize);

        assertEquals(pageSize, todos.size());
        final Todo first = todos.get(0);
        assertNotNull(first);
        final Todo second = todos.get(1);
        assertNotNull(second);
        final Todo third = todos.get(2);
        assertNotNull(third);
        final Todo fourth = todos.get(3);
        assertNotNull(fourth);
        final Todo fifth = todos.get(4);
        assertNotNull(fifth);

        assertEquals(1L, first.getId());
        assertEquals("todo-1", first.getTitle());
        assertEquals(3, first.getComments().size());

        final Set<Long> pageIds = first.getComments().stream().map(TodoComment::getId).collect(Collectors.toSet());
        Assertions.assertTrue(pageIds.containsAll(List.of(2L, 3L, 1L)));

        assertEquals(10L, second.getId());
        assertEquals("todo-10", second.getTitle());
        assertEquals(0, second.getComments().size());

        assertEquals(11L, third.getId());
        assertEquals("todo-11", third.getTitle());
        assertEquals(0, third.getComments().size());


        assertEquals(13L, fifth.getId());
        assertEquals("todo-13", fifth.getTitle());
        assertEquals(6, fifth.getComments().size());
        final Set<Long> pageFifthIds = fifth.getComments().stream().map(TodoComment::getId).collect(Collectors.toSet());
        Assertions.assertTrue(pageFifthIds.containsAll(List.of(12L, 7L, 8L, 9L, 10L, 11L)));

        // now execute one more query to get the attachment

        final TodoComment todoComment1 = first.getComments().stream().filter(todoComment -> todoComment.getId() == 1L).findFirst().orElse(null);
        assertNotNull(todoComment1);

       em.getTransaction().commit();
    }

    private List<Todo> getTodosPage(final int page, final int pageSize) {
        final int firstRecord = page * pageSize + 1;
        final int lastRecord = firstRecord + pageSize - 1;

        List<Todo> todos = em.createNamedQuery("TodoWithCommentByRank")
                .setParameter("firstRecord", firstRecord)
                .setParameter("lastRecord", lastRecord)
                //.setHint(QueryHints.HINT_READONLY, "true")
                .unwrap(NativeQuery.class)
                //.addEntity( "td", Todo.class )
                //.addEntity( "tdc", TodoComment.class )
                //.setResultTransformer(new DistinctDetachTodoResultTransformer(em))
                .setResultListTransformer(new DistinctDetachTodoResultTransformer(em))
                .getResultList();

        return todos;
    }

    @Test
    public void should_throw_exception_when_load_fields_lazy() {
        em.getTransaction().begin();

        int page = 0;
        int pageSize = 5;

        List<Todo> todos = getTodosPage(page, pageSize);

        final Todo todo1 = todos.stream()
                .filter(td -> td.getId() == 1L)
                .findFirst()
                .orElse(null);
        assertNotNull(todo1);

        final TodoComment todoComment = todo1.getComments()
                .stream().filter(tdc -> tdc.getId() == 2L)
                .findFirst()
                .orElse(null);

        assertNotNull(todoComment);

        final HibernateException hibernateException = assertThrows(HibernateException.class, todoComment::getAttachment);
        assertEquals("entity is not associated with the session: null", hibernateException.getMessage());

        em.getTransaction().commit();
    }

    @Test
    @Order(3)
    public void should_fetch_and_paginate_using_denseRank_and_merge() {
        int page = 0;
        int pageSize = 5;

        List<Todo> todos = getTodosPage(page, pageSize);

        final Todo todo1 = todos.stream().filter(td -> td.getId() == 1L).findFirst().orElse(null);
        assertNotNull(todo1);
        assertEquals(3, todo1.getComments().size());
        final TodoComment todoComment1 = todo1.getComments()
                .stream()
                .filter(tdc -> tdc.getId() == 1L)
                .findFirst()
                .orElse(null);
        assertNotNull(todoComment1);
        todo1.removeComment(todoComment1);
        final Todo todo10 = todos.stream()
                .filter(td -> td.getId() == 10L)
                .findFirst()
                .orElse(null);
        assertNotNull(todo10);
        todo10.addComment(TodoComment.of(598L, "Awesome!"));


        em.getTransaction().begin();
        em.merge(todo1);
        em.merge(todo10);
        em.getTransaction().commit();
    }

}
