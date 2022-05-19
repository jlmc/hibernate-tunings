package io.costax.hibernatetunning.paginantion.hibernate6;

import io.costax.hibernatetunning.paginantion.data.DataPopulate;
import io.costax.hibernatetunning.tasks.Todo;
import io.costax.hibernatetunning.tasks.TodoSummary;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class PaginantionTest {

    @JpaContext
    public JpaProvider provider;

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    void setUp() {
        DataPopulate.executePopulate(provider.em());
    }

    @AfterEach
    void tearDown() {
        DataPopulate.executeCleanUp(provider.em());
    }

    @Test
    public void should_set_max_results_JPQL() {
        List<Todo> todos = provider.em().createQuery("select d from Todo d order by d.id", Todo.class)
                .setMaxResults(10)
                .getResultList();

        assertEquals(10, todos.size());
        assertEquals("todo-1", todos.get(0).getTitle());
        assertEquals("todo-10", todos.get(9).getTitle());
    }

    @Test
    public void shouldUseSetFirstResultAndSetMaxResultsJPQL() {
        List<Todo> developers = provider.em().createQuery("select d from Todo d order by d.id", Todo.class)
                .setFirstResult(10)
                .setMaxResults(10)
                .getResultList();

        assertEquals(10, developers.size());
        assertEquals("todo-11", developers.get(0).getTitle());
        assertEquals("todo-20", developers.get(9).getTitle());
    }

    @Test
    public void shouldUseSetFirstResultAndSetMaxResultsJPQLToDTOProjection() {
        List<TodoSummary> todos = em
                .createQuery(
                        """
                                select 
                                new io.costax.hibernatetunning.tasks.TodoSummary(
                                   d.id, d.title, count (pl)
                                ) 
                                from Todo d left join d.comments pl
                                group by d.id, d.title order by d.id
                                """
                        , TodoSummary.class)
                .setFirstResult(0)
                .setMaxResults(10)
                .getResultList();

        assertEquals(10, todos.size());
        assertEquals("todo-1", todos.get(0).getTitle());
        assertEquals(3L, todos.get(0).getNumOfComments());
        assertEquals("todo-10", todos.get(9).getTitle());
        assertEquals(0L, todos.get(9).getNumOfComments());
    }

    @Test
    public void usingNativeQuery() {

        List<Tuple> todos = provider.em()
                                    .createNativeQuery(
                        "select d.* from tasks.todo d order by d.id", Tuple.class)
                                    .setFirstResult(0)
                                    .setMaxResults(10)
                                    .getResultList();

        // Note That we can use also the Entity instead of a Tuple

        assertEquals(10, todos.size());
        assertEquals("todo-1", todos.get(0).get("title", String.class));
        assertEquals("todo-10", todos.get(9).get("title", String.class));
    }

    /**
     * Hibernate will issue the following warning message:
     * <p>
     * HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!
     * <p>
     * This is because Hibernate wants to fetch entities fully along with their collections as
     * indicated by the JOIN FETCH clause while the SQL-level pagination could truncate the ResultSet
     * possibly leaving a parent Developer entity with fewer elements in the comments collection.
     * <p>
     * The problem with the HHH000104 warning is that Hibernate will fetch the product of
     * Developer and ProgramingLanguage entities, and due to the result set size,
     * the query response time is going to be significant.
     */
    @Test
    public void shouldPaginateInMemoryWhenWeUseJoinFetch() {
        List<Todo> developers = provider.em()
                .createQuery("select distinct d from Todo d left join fetch d.comments order by d.id", Todo.class)
                .setFirstResult(0)
                .setMaxResults(10)
                .getResultList();

        assertEquals(10, developers.size());
        assertEquals("todo-1", developers.get(0).getTitle());
        assertEquals("todo-10", developers.get(9).getTitle());
    }

    /**
     * In order to work around the previous limitation (HHH000104), we have to use a Window Function query:
     */
    @Test
    public void paginationUsingNativeQueryWithWindowFunction() {
        // Check the example in the class PaginationOneToManyWithLazyFieldTest
    }

}
