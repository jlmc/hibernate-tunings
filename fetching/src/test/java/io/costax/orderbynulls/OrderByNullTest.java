package io.costax.orderbynulls;

import io.costax.model.Issue;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * How to Handle NULL Values while Ordering Query Results in JPQL
 */
@JpaTest(persistenceUnit = "it")
public class OrderByNullTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(io.costax.queryhintfetchsize.QueryHintFetchSizeTest.class);

    @PersistenceContext
    public EntityManager em;


    /* *************************************
     *
     * Using Hibernate feature: NULLS FIRST or NULLS LAST with JPQL
     *
     ************************************* */

    @Test
    public void nulls_firsts_jpql() {

        // select * from issue order by title nulls first
        final List<Issue> resultList = em
                .createQuery("select i from Issue i order by i.title desc NULLS FIRST", Issue.class)
                .getResultList();

        assertNotNull(resultList);
    }

    @Test
    public void nulls_lasts_jpql() {

        // select * from issue order by title nulls last
        final List<Issue> resultList = em
                .createQuery("select i from Issue i order by i.title desc NULLS LAST", Issue.class)
                .getResultList();

        assertNotNull(resultList);
    }

    /* *************************************
     *
     * Using alternative 'order With Switch Case' or 'order with coalesce' with criteria
     *
     ************************************* */

    @Test
    public void nulls_first_criteria_with_switch_case() {

        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<Issue> cq = cb.createQuery(Issue.class);
        final Root<Issue> from = cq.from(Issue.class);

        final Order orderWithSwitchCase = cb.desc(
                cb.selectCase()
                        .when(cb.isNotNull(from.get("description")), from.get("description"))
                        .otherwise(cb.literal("aaaaaaaa"))
        );

        cq.orderBy(orderWithSwitchCase);

        final List<Issue> resultList = em.createQuery(cq).getResultList();

        assertNotNull(resultList);
    }

    @Test
    public void nulls_firts_criteria_with_coalesce() {

        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<Issue> cq = cb.createQuery(Issue.class);
        final Root<Issue> from = cq.from(Issue.class);


        final Path<String> alternative = from.get("title");
        final Expression<String> coalesce = cb.coalesce(from.get("description"), alternative);
        final Order orderWithCoalesce = cb.desc(coalesce);

        cq.orderBy(orderWithCoalesce);

        final List<Issue> resultList = em.createQuery(cq).getResultList();

        assertNotNull(resultList);
    }

    @Test
    public void nulls_firts_criteria_with_coalesce_with_dates() {
        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<Issue> cq = cb.createQuery(Issue.class);
        final Root<Issue> from = cq.from(Issue.class);

        final OffsetDateTime maxValue = getMaxPostgresOffsetDateTime();
        final Path<OffsetDateTime> createAt = from.get("createAt");
        final Expression<OffsetDateTime> coalesce1 = cb.coalesce(createAt, maxValue);

        final Order orderWithCoalesce = cb.desc(coalesce1);

        cq.orderBy(orderWithCoalesce);

        final List<Issue> resultList = em.createQuery(cq).getResultList();

        assertNotNull(resultList);
    }

    private OffsetDateTime getMaxPostgresOffsetDateTime() {
        final LocalDate maxPostgresAcceptedDate = LocalDate.of(294276, 12, 31);
        final LocalDateTime dt = LocalDateTime.of(maxPostgresAcceptedDate, LocalTime.MAX);
        return OffsetDateTime.of(dt, ZoneOffset.UTC);
    }
}
