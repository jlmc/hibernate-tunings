package io.costax.hibernatetunning.distincts;

import io.costa.hibernatetunings.entities.project.Issue;
import io.costa.hibernatetunings.entities.project.Project;
import io.costax.rules.EntityManagerProvider;
import org.hamcrest.Matchers;
import org.hibernate.jpa.QueryHints;
import org.junit.*;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PassDistinctThroughHintTest {

    private static final int NUMBER_OF_ISSUES = 25;

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @After
    public void cleanup() {
        provider.beginTransaction();

        provider.em().createQuery("delete from Issue ").executeUpdate();
        provider.em().createQuery("delete from Project ").executeUpdate();

        provider.commitTransaction();
    }

    @Before
    public void populate() {
        provider.beginTransaction();
        final EntityManager em = provider.em();

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

        provider.commitTransaction();
    }

    @Test
    public void t00_no_distinct() {
        final EntityManager em = provider.em();

        List<Project> projects = em.createQuery(
                "SELECT a FROM Project a JOIN FETCH a.issues", Project.class)
                .getResultList();

        for (Project a : projects) {
            System.out.println("-- " + a.getId() + " " + a.getTitle() + " wrote " + a.getIssues().size() + " issues.");
        }

        // We get NUMBER_OF_ISSUES projects instance but the data base contains only one project record
        Assert.assertThat(projects, Matchers.hasSize(NUMBER_OF_ISSUES));
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
    public void t01_only_return_unique_results() {
        final EntityManager em = provider.em();

        List<Project> projects = em.createQuery(
                "SELECT DISTINCT a FROM Project a JOIN FETCH a.issues", Project.class)
                .getResultList();

        for (Project a : projects) {
            System.out.println("-- " + a.getId() + " " + a.getTitle() + " wrote " + a.getIssues().size() + " issues.");
        }

        Assert.assertThat(projects, Matchers.hasSize(1));
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
    public void t02_pass_distinct_through_hint() {

        final EntityManager em = provider.em();

        List<Project> projects = em.createQuery(
                "SELECT DISTINCT a FROM Project a JOIN FETCH a.issues", Project.class)

                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)

                .getResultList();

        for (Project a : projects) {
            System.out.println("-- " + a.getId() + " " + a.getTitle() + " wrote " + a.getIssues().size() + " issues.");
        }

        Assert.assertThat(projects, Matchers.hasSize(1));
    }

    @Test
    public void t03_pass_distinct_through_hint_ignored_because_no_distinct_in_jpql_query() {

        final EntityManager em = provider.em();

        List<Project> projects = em.createQuery(
                "SELECT a FROM Project a JOIN FETCH a.issues", Project.class)

                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)

                .getResultList();

        for (Project a : projects) {
            System.out.println("-- " + a.getId() + " " + a.getTitle() + " wrote " + a.getIssues().size() + " issues.");
        }

        // We get NUMBER_OF_ISSUES projects instance but the data base contains only one project record
        Assert.assertThat(projects, Matchers.hasSize(NUMBER_OF_ISSUES));
    }
}

