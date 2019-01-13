package io.costax.hibernatetuning.castoperator;

import io.costax.hibernatetunig.model.Issue;
import io.costax.hibernatetunig.model.Project;
import io.costax.hibernatetunig.model.projections.IssueSummary;
import io.costax.rules.EntityManagerProvider;
import org.hamcrest.Matchers;
import org.junit.*;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.*;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CastOperatorDatesWithMetamodelTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    private final LocalDate startSearch = LocalDate.of(2018, 1, 20);
    private final LocalDate endSearch = LocalDate.of(2018, 2, 1);

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

        final Project castingDates = Project.of("Casting Dates");

        final OffsetDateTime startProjecTime = OffsetDateTime.of(
                LocalDateTime.of(
                        LocalDate.of(2018, 1, 1),
                        LocalTime.of(8, 15)),
                ZoneOffset.UTC);

        IntStream.range(0, 100)
                .mapToObj(i -> Issue.of(castingDates, "Issue - " + i, startProjecTime.plusDays(i)))
                .forEachOrdered(castingDates::addIssue);

        em.persist(castingDates);

        provider.commitTransaction();
    }

    @Test
    public void should_find_issue_summary_using_jpql() {

        final EntityManager em = provider.em();

        final List<IssueSummary> issues = em.createQuery(
                "select new io.costax.hibernatetunig.model.projections.IssueSummary(" +
                        "   i.id, " +
                        "   i.title," +
                        "   function('date', i.createAt) " +
                        ") " +
                        "from Issue i " +
                        "where function('date', i.createAt) > :start " +
                        "   and function('date', i.createAt) < :end " +
                        "order by function('date_part', 'dow', i.createAt) asc, i.id asc ", IssueSummary.class)
                .setParameter("start", startSearch)
                .setParameter("end", endSearch)
                .getResultList();

        assertResultIssues(issues);
    }

    @Test
    public void should_find_issue_summary_using_criteria() {
        final EntityManager em = provider.em();

        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<IssueSummary> cq = cb.createQuery(IssueSummary.class);
        final Root<Issue> from = cq.from(Issue.class);

        // NOTE: If we need the functions results in the queries projection then we must register the functions
        cq.select(cb.construct(
            IssueSummary.class,
            from.get("id"),
            from.get("title"),
            cb.function("date", LocalDate.class, from.get("createAt"))
        ))
        .where(
            cb.greaterThan(
                    cb.function("date", LocalDate.class, from.get("createAt")),
                    startSearch),
            cb.lessThan(
                    cb.function("date", LocalDate.class, from.get("createAt")),
                    endSearch)
        )
        .orderBy(
                // 'dow' - The day of the week as Sunday (0) to Saturday (6)
            cb.asc(cb.function("date_part", Integer.class, cb.literal("dow"), from.get("createAt"))),
            cb.asc(from.get("id")));

        final List<IssueSummary> issues = em.createQuery(cq).getResultList();

        assertResultIssues(issues);
    }

    @Test
    public void should_find_issue_summary_using_jpql_using_cast() {
        final EntityManager em = provider.em();

        // NOTE: we are using Java types: cast( i.createAt as LocalDate ) is the same as cast( i.createAt as java.time.LocalDate )

        final List<IssueSummary> issues = em.createQuery(
                "select new io.costax.hibernatetunig.model.projections.IssueSummary(" +
                        "   i.id, " +
                        "   i.title," +
                        "   cast( i.createAt as LocalDate ) " +
                        ") " +
                        "from Issue i " +
                        "where cast( i.createAt as LocalDate ) > :start " +
                        "   and cast(  i.createAt as LocalDate ) < :end " +
                        "order by function('date_part', 'dow', i.createAt) asc, i.id asc ", IssueSummary.class)
                .setParameter("start", startSearch)
                .setParameter("end", endSearch)
                .getResultList();

        assertResultIssues(issues);
    }

    @Test
    public void should_find_issue_summary_using_native_query() {

        final EntityManager em = provider.em();

        List<IssueSummary> issues = em.createNativeQuery(
                "select " +
                "   i.id as id, " +
                "   i.title as title, " +
                "   cast(i.create_at as date) as day " +
                "from " +
                "   issue i " +
                "where cast(i.create_at as date) > :startp " +
                "   and cast(i.create_at as date) < :endp " +
                "order by date_part('dow',i.create_at) asc, i.id asc ",
                "IssueSummaryMapper")
             .setParameter("startp", startSearch)
             .setParameter("endp", endSearch)
             .getResultList();

        assertResultIssues(issues);
    }

    private void assertResultIssues(final List<IssueSummary> issues) {
        assertThat(issues, Matchers.hasSize(11));
        IssueSummary issueSummary = issues.get(0);
        assertNotNull(issueSummary);

        assertThat(issueSummary.getTitle(), is("Issue - 20"));
        assertThat(issueSummary.getDay(), notNullValue());
        assertThat(issueSummary.getDay(), is(LocalDate.of(2018, 1, 21)));
        assertThat(issueSummary.getDay().getDayOfWeek(), is(DayOfWeek.SUNDAY));


        assertNotNull(issues.get(issues.size() - 1));
        issueSummary = issues.get(issues.size() - 1);
        assertNotNull(issueSummary);
        assertThat(issueSummary.getTitle(), is("Issue - 26"));
        assertThat(issueSummary.getDay(), notNullValue());
        assertThat(issueSummary.getDay(), is(LocalDate.of(2018, 1, 27)));
        assertThat(issueSummary.getDay().getDayOfWeek(), is(DayOfWeek.SATURDAY));
    }
}
