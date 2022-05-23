package io.costax.hibernatetuning.castoperator;

import io.costax.hibernatetunig.model.Issue;
import io.costax.hibernatetunig.model.Issue_;
import io.costax.hibernatetunig.model.Project;
import io.costax.hibernatetunig.model.projections.IssueSummary;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CastOperatorDatesTest {

    private final LocalDate startSearch = LocalDate.of(2018, 1, 20);
    private final LocalDate endSearch = LocalDate.of(2018, 2, 1);
    @JpaContext
    public JpaProvider provider;

    @BeforeEach
    public void populate() {
        provider.doInTx(em -> {

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
        });


    }

    @Test
    public void should_find_issue_summary_using_jpql() {
        final EntityManager em = provider.em();

        final List<IssueSummary> issues = em.createQuery(
               """
                select new io.costax.hibernatetunig.model.projections.IssueSummary(
                    i.id,
                    i.title,
                    function('date', i.createAt)
                )
                from Issue i
                where function('date', i.createAt) > :start
                   and function('date', i.createAt) < :end
                order by function('date_part', 'dow', i.createAt) asc, i.id asc
                """, IssueSummary.class)
                .setParameter("start", startSearch)
                .setParameter("end", endSearch)
                .getResultList();

        assertResultIssues(issues);

        em.close();
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
                from.get(Issue_.id),
                from.get(Issue_.title),
                cb.function("date", LocalDate.class, from.get(Issue_.createAt))
        ))
                .where(
                        cb.greaterThan(
                                cb.function("date", LocalDate.class, from.get(Issue_.createAt)),
                                startSearch),
                        cb.lessThan(
                                cb.function("date", LocalDate.class, from.get(Issue_.createAt)),
                                endSearch)
                )
                .orderBy(
                        // 'dow' - The day of the week as Sunday (0) to Saturday (6)
                        cb.asc(cb.function("date_part", Integer.class, cb.literal("dow"), from.get(Issue_.createAt))),
                        cb.asc(from.get(Issue_.id)));

        final List<IssueSummary> issues = em.createQuery(cq).getResultList();

        assertResultIssues(issues);

        em.close();
    }

    @Test
    public void should_find_issue_summary_using_jpql_using_cast() {
        final EntityManager em = provider.em();

        // NOTE: we are using Java types: cast( i.createAt as LocalDate ) is the same as cast( i.createAt as java.time.LocalDate )

        final List<IssueSummary> issues = em.createQuery(
                """
                        select new io.costax.hibernatetunig.model.projections.IssueSummary(
                                i.id,
                                i.title,
                                cast( i.createAt as LocalDate ) 
                                )
                        from Issue i 
                        where cast( i.createAt as LocalDate ) > :start 
                            and cast(  i.createAt as LocalDate ) < :end 
                        order by function('date_part', 'dow', i.createAt) asc, i.id asc
                        """
                , IssueSummary.class)
                .setParameter("start", startSearch)
                .setParameter("end", endSearch)
                .getResultList();

        em.close();

        assertResultIssues(issues);
    }

    @Test
    public void should_find_issue_summary_using_native_query() {
        final EntityManager em = provider.em();

        List<IssueSummary> issues = em.createNativeQuery(
                """
                        select
                           i.id as id, 
                           i.title as title, 
                           cast(i.create_at as date) as day 
                        from issue i 
                        where cast(i.create_at as date) > :startp 
                           and cast(i.create_at as date) < :endp 
                        order by date_part('dow',i.create_at) asc, i.id asc
                        """
                , "IssueSummaryMapper")
                .setParameter("startp", startSearch)
                .setParameter("endp", endSearch)
                .getResultList();

        em.close();

        assertResultIssues(issues);
    }

    private void assertResultIssues(final List<IssueSummary> issues) {
        Assertions.assertTrue(11 == issues.size());
        IssueSummary issueSummary = issues.get(0);
        assertNotNull(issueSummary);

        assertEquals("Issue - 20", issueSummary.getTitle());
        assertNotNull(issueSummary.getDay());
        assertEquals(LocalDate.of(2018, 1, 21), issueSummary.getDay());
        assertEquals(DayOfWeek.SUNDAY, issueSummary.getDay().getDayOfWeek());


        assertNotNull(issues.get(issues.size() - 1));
        issueSummary = issues.get(issues.size() - 1);
        assertNotNull(issueSummary);
        assertEquals("Issue - 26", issueSummary.getTitle());
        assertNotNull(issueSummary.getDay());
        assertEquals(LocalDate.of(2018, 1, 27), issueSummary.getDay());
    }
}
