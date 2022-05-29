package io.costax.hibernatetunning.distincts;

import io.costax.hibernatetunings.entities.project.Issue;
import io.costax.hibernatetunings.entities.project.Project;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.annotation.Sql;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.hibernate.annotations.QueryHints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

import static io.github.jlmc.jpa.test.annotation.Sql.Phase.AFTER_TEST_METHOD;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JpaTest(persistenceUnit = "it")
@Sql(phase = AFTER_TEST_METHOD, statements = {"delete from issue", "delete from project"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class PassDistinctThroughHintTest {

    private static final int NUMBER_OF_ISSUES = 25;

    @JpaContext
    public JpaProvider provider;


    @BeforeEach
    public void populate() {
        final EntityManager em = provider.em();
        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        em.createQuery("delete from Issue ").executeUpdate();
        em.createQuery("delete from Project ").executeUpdate();

        final DateTimeFormatter isoOffsetDateTime = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        final Project castingDates = Project.of("\"Pass Distinct Through Hint\"");

        final OffsetDateTime startProjecTime = OffsetDateTime.of(
                LocalDateTime.of(
                        LocalDate.of(2018, 1, 1),
                        LocalTime.of(8, 15)),
                ZoneOffset.UTC);

        IntStream.range(0, NUMBER_OF_ISSUES)
                 .mapToObj(i -> Issue.of(castingDates, "Issue - " + isoOffsetDateTime.format(startProjecTime.plusDays(i))))
                 .forEachOrdered(castingDates::addIssue);

        em.persist(castingDates);

        tx.commit();
        em.close();
    }

    @Test
    @Order(1)
    public void execute_jpql_query_without_distinct() {
        final EntityManager em = provider.em();

        List<Project> projects = em.createQuery(
                                           "SELECT a FROM Project a JOIN FETCH a.issues", Project.class)
                                   .getResultList();

        for (Project a : projects) {
            System.out.println("-- " + a.getId() + " " + a.getTitle() + " wrote " + a.getIssues().size() + " issues.");
        }

        em.close();

        assertEquals(projects.size(), NUMBER_OF_ISSUES);
    }

    /**
     * <p>
     * We can add the DISTINCT keyword to your query to tell Hibernate to return each Project entity only once.
     * </p>
     *
     * <p>
     * But as we can see in the log messages, Hibernate also adds the DISTINCT keyword to the SQL query.
     * This is often not intended and might result in an efficient database query.
     * </p>
     */
    @Test
    @Order(2)
    public void query_with_distinct_returning_unique_results() {
        final EntityManager em = provider.em();

        List<Project> projects = em.createQuery(
                                           "SELECT DISTINCT a FROM Project a JOIN FETCH a.issues", Project.class)
                                   .getResultList();

        for (Project a : projects) {
            System.out.println("-- " + a.getId() + " " + a.getTitle() + " wrote " + a.getIssues().size() + " issues.");
        }

        em.close();

        assertEquals(projects.size(), 1);
    }

    /**
     * Since Hibernate 5.2, we can prevent Hibernate from adding the DISTINCT keyword to the SQL statement by setting
     * the query hint hibernate.query.passDistinctThrough to false.
     *
     * <p>
     * The easiest way to set this hint is to use the constant defined in Hibernate’s
     * org.hibernate.jpa.QueryHints and org.hibernate.annotations.QueryHints class
     * </p>
     */
    @Test
    @Order(3)
    public void using_hibernate_HINT_PASS_DISTINCT_THROUGH_in_jpql_with_the_distinct_keyword() {
        final EntityManager em = provider.em();

        List<Project> projects = em.createQuery(
                                           "SELECT DISTINCT a FROM Project a JOIN FETCH a.issues", Project.class)
                                   // .setHint("hibernate.query.passDistinctThrough", false)
                                   .setHint("hibernate.query.passDistinctThrough", false)
                                   .getResultList();

        for (Project a : projects) {
            System.out.println("-- " + a.getId() + " " + a.getTitle() + " wrote " + a.getIssues().size() + " issues.");
        }

        em.close();

        assertEquals(projects.size(), 1);
    }

    @Test
    @Order(4)
    public void using_hibernate_HINT_PASS_DISTINCT_THROUGH_in_jpql_query_without_the_distinct_keyword() {
        final EntityManager em = provider.em();

        List<Project> projects = em.createQuery(
                                           "SELECT a FROM Project a JOIN FETCH a.issues", Project.class)
                                   .setHint("hibernate.query.passDistinctThrough", false)
                                   .getResultList();

        for (Project a : projects) {
            System.out.println("-- " + a.getId() + " " + a.getTitle() + " wrote " + a.getIssues().size() + " issues.");
        }

        em.close();


        // We get NUMBER_OF_ISSUES projects instance but the data base contains only one project record
        assertEquals(projects.size(), NUMBER_OF_ISSUES);
    }
}
