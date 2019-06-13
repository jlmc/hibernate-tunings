package io.costax.orderbynulls;

import io.costax.model.Issue;
import io.costax.rules.EntityManagerProvider;
import io.costax.rules.Watcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.time.*;
import java.util.List;


/**
 * How to Handle NULL Values while Ordering Query Results in JPQL
 */
public class OrderByNullTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(io.costax.queryhintfetchsize.QueryHintFetchSizeTest.class);

    @Rule
    public Watcher watcher = Watcher.timer(LOGGER);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");


    /* *************************************
     *
     * Using Hibernate feature: NULLS FIRST or NULLS LAST with JPQL
     *
     ************************************* */

    @Test
    public void nulls_firsts_jpql() {

        // select * from issue order by title nulls first
        final List<Issue> resultList =
                provider.em()
                        .createQuery(
                                "select i from Issue i order by i.title desc NULLS FIRST", Issue.class)
                        .getResultList();

        Assert.assertNotNull(resultList);
    }

    @Test
    public void nulls_lasts_jpql() {

        // select * from issue order by title nulls last
        final List<Issue> resultList =
                provider.em()
                        .createQuery(
                                "select i from Issue i order by i.title desc NULLS LAST", Issue.class)
                        .getResultList();

        Assert.assertNotNull(resultList);
    }

    /* *************************************
     *
     * Using alternative 'order With Switch Case' or 'order with coalesce' with criteria
     *
     ************************************* */

    @Test
    public void nulls_firts_criteria_with_swicth_case() {
        final EntityManager em = provider.em();

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

        Assert.assertNotNull(resultList);
    }

    @Test
    public void nulls_firts_criteria_with_coalesce() {
        final EntityManager em = provider.em();

        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<Issue> cq = cb.createQuery(Issue.class);
        final Root<Issue> from = cq.from(Issue.class);


        final Path<String> alternative = from.get("title");
        final Expression<String> coalesce = cb.coalesce(from.get("description"), alternative);
        final Order orderWithCoalesce = cb.desc(coalesce);

        cq.orderBy(orderWithCoalesce);

        final List<Issue> resultList = em.createQuery(cq).getResultList();

        Assert.assertNotNull(resultList);
    }

    @Test
    public void nulls_firts_criteria_with_coalesce_with_dates() {
        final EntityManager em = provider.em();

        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<Issue> cq = cb.createQuery(Issue.class);
        final Root<Issue> from = cq.from(Issue.class);

        final OffsetDateTime maxValue = getMaxPostgresOffsetDateTime();
        final Path<OffsetDateTime> createAt = from.get("createAt");
        final Expression<OffsetDateTime> coalesce1 = cb.coalesce(createAt, maxValue);

        final Order orderWithCoalesce = cb.desc(coalesce1);

        cq.orderBy(orderWithCoalesce);

        final List<Issue> resultList = em.createQuery(cq).getResultList();

        Assert.assertNotNull(resultList);
    }

    private OffsetDateTime getMaxPostgresOffsetDateTime() {
        final LocalDate maxPostgresAcceptedDate = LocalDate.of(294276, 12, 31);
        final LocalDateTime dt = LocalDateTime.of(maxPostgresAcceptedDate, LocalTime.MAX);
        return OffsetDateTime.of(dt, ZoneOffset.UTC);
    }
}