package io.costax.hibernatetunnig.sqlslow;

import io.costax.hibernatetunnig.graphs.entity.Author;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.LongStream;

/**
 * <H1>Hibernate slow query log</H1>
 *
 * <p>
 * In this Test Case I’m going to show you how you can activate the slow query log when using JPA and Hibernate.
 * </p>
 * <p>
 * This slow query log feature has been available since Hibernate ORM 5.4.5 and notifies you when the execution time of a given JPQL,
 * Criteria API or native SQL query exceeds a certain threshold value you have previously configured.
 * </p>
 * <br>
 * <br>
 *
 * <H2>Hibernate slow query log</H2>
 *
 * <p>
 *  In order to activate the slow query log, you need to set the {@code hibernate.session.events.log.LOG_QUERIES_SLOWER_THAN_MS} property to a value that’s greater than 0, representing the query execution threshold.
 * </p>
 * <p>
 * In our case, any query that takes more than 25 milliseconds will trigger the slow query log entry.
 * </p>
 * <p>
 * If you’re using Spring Boot, you can set this Hibernate setting in the application.properties configuration file:
 * </p>
 *
 * <pre>
 *
 *     spring.jpa.properties.hibernate.session.events.log.LOG_QUERIES_SLOWER_THAN_MS=25
 *
 * </pre>
 *
 * <p>
 * If you’re using Java EE, you can set it via the persistence.xml configuration file:
 * </p>
 *
 * <pre>
 *     <property
 *          name="hibernate.session.events.log.LOG_QUERIES_SLOWER_THAN_MS"
 *          value="25"/>
 * </pre>
 *
 * <p>
 *  And, Hibernate will log all SQL queries that took more than 25 milliseconds to be executed. In order to see the slow query log entry, you need to make sure you have the following logger name set to at least the info level:
 * </p>
 *
 * <pre>
 *     <logger name="org.hibernate.SQL_SLOW" level="info"/>
 * </pre>
 *
 * <p>
 *     To see how the Hibernate slow query log works, we can check the Hibernate {@link org.hibernate.engine.jdbc.internal.ResultSetReturnImpl} class which is used for all SQL query
 * </p>
 */
@JpaTest(persistenceUnit = "it")
public class ShowQueriesLogTest {

    @JpaContext
    public JpaProvider provider;

    @BeforeEach
    public void setUp() {
        provider.doInTx(em ->
                LongStream.rangeClosed(1L, 5001)
                .mapToObj(i -> new Author(i, "Author-" + i))
                .forEach(em::persist));
    }

    @AfterEach
    public void tearDown() {
        provider.doInTx(em -> em.createQuery("delete from Author").executeUpdate());
    }

    @Test
    public void should_log_slow_queries() {
        final EntityManager em = provider.em();

        final List<Author> authors = em.createQuery("select a from Author a", Author.class).getResultList();
        System.out.println("---> Please check the logger and try to find  \"[org.hibernate.SQL_SLOW] - SlowQuery:\" Entries");

        em.close();

        Assertions.assertNotNull(authors);
    }
}
