package io.costax.batching;

import io.costax.rules.EntityManagerProvider;
import io.costax.rules.Watcher;
import org.hamcrest.Matchers;
import org.junit.Ignore;
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

import static io.costax.batching.Review.Rating;
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
        final List<Review> reviews = provider.em().createQuery("select r from Review r where r.rating = :code", Review.class)
                .setParameter("code", Review.Rating.TWO)
                .getResultList();

        assertThat(reviews, Matchers.hasSize(2));
    }

    @Test
    public void filterCustomEnumInJPQLWithEnumInQuery() {
        final List<Review> reviews = provider.em().createQuery("select r from Review r " +
                        "where r.rating = :code " +
                        "and r.rating <> " + Rating.THREE.getCode()
                , Review.class)
                .setParameter("code", Rating.TWO)
                .getResultList();

        assertThat(reviews, Matchers.hasSize(2));
    }

    @Test
    public void filterCustomEnumInCriteria() {
        final EntityManager em = provider.em();
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Review> cq = cb.createQuery(Review.class);
        final Root<Review> r = cq.from(Review.class);

        //@formatter:off
        List<Review> reviews = em.createQuery(
            cq.where(
                //  Review.rating > 11
                cb.greaterThan(r.get("rating"), Rating.ONE.getCode())
            ).orderBy(cb.desc(r.get("rating")), cb.asc(r.get("id"))))
        .getResultList();
        //@formatter:on

        assertThat(reviews, Matchers.hasSize(3));
        final List<Integer> ids = reviews.stream().map(Review::getId).collect(Collectors.toList());
        assertThat(ids, Matchers.contains(3, 2, 4));
    }

    @Test
    public void filterCustomEnumInCriteriaDirectBinding() {
        final EntityManager em = provider.em();
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Review> cq = cb.createQuery(Review.class);
        final Root<Review> r = cq.from(Review.class);

        //@formatter:off
        List<Review> reviews = em.createQuery(
            cq.where(
                // with direct enum as parameter hibernate will go to use a ? sql parameter
                // Review.rating > ?
                cb.greaterThan(r.get("rating"), Rating.ONE)
            ).orderBy(cb.desc(r.get("rating")), cb.asc(r.get("id"))))
        .getResultList();
        //@formatter:on

        assertThat(reviews, Matchers.hasSize(3));
        final List<Integer> ids = reviews.stream().map(Review::getId).collect(Collectors.toList());
        assertThat(ids, Matchers.contains(3, 2, 4));
    }

    @Test
    public void filterCustomEnumInCriteriaDirectBindingAndWithMetamodel() {
        final EntityManager em = provider.em();
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Review> cq = cb.createQuery(Review.class);
        final Root<Review> r = cq.from(Review.class);

        //@formatter:off
        List<Review> reviews = em.createQuery(
            cq.where(
                // with direct enum as parameter hibernate will go to use a ? sql parameter
                // report.rating > ?
                cb.greaterThan(r.get(io.costax.batching.Review_.rating), Rating.ONE)
            )
            .orderBy(cb.desc(r.get(io.costax.batching.Review_.rating)), cb.asc(r.get(io.costax.batching.Review_.id))))
        .getResultList();
        //@formatter:on

        assertThat(reviews, Matchers.hasSize(3));
        final List<Integer> ids = reviews.stream().map(Review::getId).collect(Collectors.toList());
        assertThat(ids, Matchers.contains(3, 2, 4));
    }

    @Test
    public void filterCustomEnumInCriteriaDirectBindingAndWithMetamodelAndBuildedParameter() {
        final EntityManager em = provider.em();
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Review> cq = cb.createQuery(Review.class);
        final Root<Review> r = cq.from(Review.class);

        final ParameterExpression<Rating> notInrating = cb.parameter(Rating.class, "not_in_rating");

        //@formatter:off
        List<Review> reports = em.createQuery(
            cq.where(
                // with direct enum as parameter hibernate will go to use a ? sql parameter
                // report.rating > ?
                cb.greaterThan(r.get(io.costax.batching.Review_.rating), Rating.ONE),
                // report.rating <> ?
                cb.notEqual(r.get(io.costax.batching.Review_.rating), notInrating)
            )
            .orderBy(cb.desc(r.get(io.costax.batching.Review_.rating)), cb.asc(r.get(io.costax.batching.Review_.id))))
        .setParameter("not_in_rating", Rating.THREE)
        .getResultList();
        //@formatter:on

        assertThat(reports, Matchers.hasSize(2));
        final List<Integer> ids = reports.stream().map(Review::getId).collect(Collectors.toList());
        assertThat(ids, Matchers.contains(2, 4));
    }

    @Test
    public void filterCustomEnumInCriteriaAndBuildedParameter() {
        final EntityManager em = provider.em();
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Review> cq = cb.createQuery(Review.class);
        final Root<Review> r = cq.from(Review.class);

        final ParameterExpression<Integer> notInRating = cb.parameter(Integer.class, "not_in_rating");

        //@formatter:off
        List<Review> reports = em.createQuery(
            cq.where(
                // with direct enum as parameter hibernate will go to use a ? sql parameter
                // report.rating > ?
                cb.greaterThan(r.get(io.costax.batching.Review_.rating), Rating.ONE),
                // report.rating <> ?
                //cb.notEqual(r.get("rating"), notInrating)
                //  and report0_.rating<>13
                cb.notEqual(r.get("rating"), Rating.THREE.getCode())
            )
            .orderBy(cb.desc(r.get(io.costax.batching.Review_.rating)), cb.asc(r.get(io.costax.batching.Review_.id))))
        //.setParameter("not_in_rating", Report.rating.DONE.getCode())
        .getResultList();
        //@formatter:on

        assertThat(reports, Matchers.hasSize(2));
        final List<Integer> ids = reports.stream().map(Review::getId).collect(Collectors.toList());
        assertThat(ids, Matchers.contains(2, 4));
    }

    // This test intends just to prove that the convert in also executed for null values
    @Test//(expected = java.lang.IllegalStateException.class)
    @Ignore
    public void converterInvokedOnNullValue() throws Throwable {


        exception.expect(IllegalStateException.class);
        exception.expectMessage("invalid rating with the code 'null'");

        try {
            final EntityManager em = provider.em();
            em.createQuery("select r from Review r where r.rating < :code or r.rating is null", Review.class)
                    .setParameter("code", Rating.ONE)
                    .getResultList();

        } catch (PersistenceException e) {
            assertThat(e.getMessage(), containsString("Error attempting to apply AttributeConverter"));

            assertThat(e.getCause(), notNullValue());

            throw e.getCause();
        }

    }
}
