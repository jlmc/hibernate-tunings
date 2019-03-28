package io.costax.batching;

import io.costax.rules.EntityManagerProvider;
import io.costax.rules.Watcher;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class CustomEnumMappingInQueryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomEnumMappingInQueryTest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Rule
    public Watcher watcher = Watcher.timer(LOGGER);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void filterCustomEnumInJPQL() {
        final List<Report> reports = provider.em().createQuery("select r from Report r where r.status = :code", Report.class)
                .setParameter("code", Report.Status.IN_PROGRESS)
                .getResultList();

        assertThat(reports, Matchers.hasSize(2));
    }

    @Test
    public void filterCustomEnumInJPQLWithEnumInQuery() {
        final List<Report> code = provider.em().createQuery("select r from Report r " +
                        "where r.status = :code " +
                        "and r.status <> " + Report.Status.DONE.getCode()
                , Report.class)
                .setParameter("code", Report.Status.IN_PROGRESS)
                .getResultList();

        assertThat(code, Matchers.hasSize(2));
    }

    @Test
    public void filterCustomEnumInCriteria() {
        final EntityManager em = provider.em();
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Report> cq = cb.createQuery(Report.class);
        final Root<Report> r = cq.from(Report.class);

        //@formatter:off
        List<Report> reports = em.createQuery(
            cq.where(
                //  report.status > 11
                cb.greaterThan(r.get("status"), Report.Status.TODO.getCode())
            ).orderBy(cb.desc(r.get("status")), cb.asc(r.get("id"))))
        .getResultList();
        //@formatter:on

        assertThat(reports, Matchers.hasSize(3));
        final List<Integer> ids = reports.stream().map(Report::getId).collect(Collectors.toList());
        assertThat(ids, Matchers.contains(3, 2, 4));
    }

    @Test
    public void filterCustomEnumInCriteriaDirectBinding() {
        final EntityManager em = provider.em();
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Report> cq = cb.createQuery(Report.class);
        final Root<Report> r = cq.from(Report.class);

        //@formatter:off
        List<Report> reports = em.createQuery(
            cq.where(
                // with direct enum as parameter hibernate will go to use a ? sql parameter
                // report.status > ?
                cb.greaterThan(r.get("status"), Report.Status.TODO)
            ).orderBy(cb.desc(r.get("status")), cb.asc(r.get("id"))))
        .getResultList();
        //@formatter:on

        assertThat(reports, Matchers.hasSize(3));
        final List<Integer> ids = reports.stream().map(Report::getId).collect(Collectors.toList());
        assertThat(ids, Matchers.contains(3, 2, 4));
    }

    @Test
    public void filterCustomEnumInCriteriaDirectBindingAndWithMetamodel() {
        final EntityManager em = provider.em();
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Report> cq = cb.createQuery(Report.class);
        final Root<Report> r = cq.from(Report.class);

        //@formatter:off
        List<Report> reports = em.createQuery(
            cq.where(
                // with direct enum as parameter hibernate will go to use a ? sql parameter
                // report.status > ?
                cb.greaterThan(r.get(Report_.status), Report.Status.TODO)
            )
            .orderBy(cb.desc(r.get(Report_.status)), cb.asc(r.get(Report_.id))))
        .getResultList();
        //@formatter:on

        assertThat(reports, Matchers.hasSize(3));
        final List<Integer> ids = reports.stream().map(Report::getId).collect(Collectors.toList());
        assertThat(ids, Matchers.contains(3, 2, 4));
    }

    @Test
    public void filterCustomEnumInCriteriaDirectBindingAndWithMetamodelAndBuildedParameter() {
        final EntityManager em = provider.em();
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Report> cq = cb.createQuery(Report.class);
        final Root<Report> r = cq.from(Report.class);

        final ParameterExpression<Report.Status> notInStatus = cb.parameter(Report.Status.class, "not_in_status");

        //@formatter:off
        List<Report> reports = em.createQuery(
            cq.where(
                // with direct enum as parameter hibernate will go to use a ? sql parameter
                // report.status > ?
                cb.greaterThan(r.get(Report_.status), Report.Status.TODO),
                // report.status <> ?
                cb.notEqual(r.get(Report_.status), notInStatus)
            )
            .orderBy(cb.desc(r.get(Report_.status)), cb.asc(r.get(Report_.id))))
        .setParameter("not_in_status", Report.Status.DONE)
        .getResultList();
        //@formatter:on

        assertThat(reports, Matchers.hasSize(2));
        final List<Integer> ids = reports.stream().map(Report::getId).collect(Collectors.toList());
        assertThat(ids, Matchers.contains(2, 4));
    }

    @Test
    public void filterCustomEnumInCriteriaAndBuildedParameter() {
        final EntityManager em = provider.em();
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Report> cq = cb.createQuery(Report.class);
        final Root<Report> r = cq.from(Report.class);

        final ParameterExpression<Integer> notInStatus = cb.parameter(Integer.class, "not_in_status");

        //@formatter:off
        List<Report> reports = em.createQuery(
            cq.where(
                // with direct enum as parameter hibernate will go to use a ? sql parameter
                // report.status > ?
                cb.greaterThan(r.get(Report_.status), Report.Status.TODO),
                // report.status <> ?
                //cb.notEqual(r.get("status"), notInStatus)
                //  and report0_.status<>13
                cb.notEqual(r.get("status"), Report.Status.DONE.getCode())
            )
            .orderBy(cb.desc(r.get(Report_.status)), cb.asc(r.get(Report_.id))))
        //.setParameter("not_in_status", Report.Status.DONE.getCode())
        .getResultList();
        //@formatter:on

        assertThat(reports, Matchers.hasSize(2));
        final List<Integer> ids = reports.stream().map(Report::getId).collect(Collectors.toList());
        assertThat(ids, Matchers.contains(2, 4));
    }

    @Test//(expected = java.lang.IllegalStateException.class)
    public void converterInvokedOnNullValue() throws Throwable {
        exception.expect(IllegalStateException.class);
        exception.expectMessage("invalid status with the code 'null'");

        try {
            final EntityManager em = provider.em();
            em.createQuery("select r from Report r where r.status < :code or r.status is null", Report.class)
                    .setParameter("code", Report.Status.TODO)
                    .getResultList();

        } catch (PersistenceException e) {
            assertThat(e.getMessage(), containsString("Error attempting to apply AttributeConverter"));

            assertThat(e.getCause(), notNullValue());

            throw e.getCause();
        }

    }
}
