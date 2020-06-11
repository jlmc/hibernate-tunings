package io.costax.batching;

import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.annotation.Sql;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Collectors;

import static io.costax.batching.Review.Rating;
import static org.assertj.core.api.Assertions.assertThat;

@JpaTest(persistenceUnit = "it")
public class CustomEnumMappingInQueryTest {

    @JpaContext
    public JpaProvider provider;

    @Test
    public void filterCustomEnumInJPQL() {
        final List<Review> reviews =
                provider.doItWithReturn(em ->
                        em.createQuery("select r from Review r where r.rating = :code", Review.class)
                            .setParameter("code", Review.Rating.TWO)
                            .getResultList());

        assertThat(reviews).hasSize(2);
    }

    @Test
    public void filterCustomEnumInJPQLWithEnumInQuery() {
        final List<Review> reviews = provider.doItWithReturn(em ->
                    em.createQuery("select r from Review r " +
                        "where r.rating = :code " +
                        "and r.rating <> " + Rating.THREE.getCode()
                    , Review.class)
                    .setParameter("code", Rating.TWO)
                    .getResultList());

        assertThat(reviews).hasSize(2);
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

        em.close();

        assertThat(reviews).hasSize(3);
        final List<Integer> ids = reviews.stream().map(Review::getId).collect(Collectors.toList());
        assertThat(ids).contains(3, 2, 4);
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

        em.close();

        assertThat(reviews).hasSize(3);
        final List<Integer> ids = reviews.stream().map(Review::getId).collect(Collectors.toList());
        assertThat(ids).contains(3, 2, 4);
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

        em.close();

        assertThat(reviews).hasSize(3);
        final List<Integer> ids = reviews.stream().map(Review::getId).collect(Collectors.toList());
        assertThat(ids).contains(3, 2, 4);
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

        em.close();

        assertThat(reports).hasSize(2);
        final List<Integer> ids = reports.stream().map(Review::getId).collect(Collectors.toList());
        assertThat(ids).contains(2, 4);
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

        em.close();

        assertThat(reports).hasSize(2);
        final List<Integer> ids = reports.stream().map(Review::getId).collect(Collectors.toList());
        assertThat(ids).contains(2, 4);
    }

    // This test intends just to prove that the convert in also executed for null values
    @DisplayName("Jpa Converter is also executed for Null values")
    @Test//(expected = java.lang.IllegalStateException.class)
    @Sql(statements = "INSERT INTO multimedia.review (id, book_id, rating, comment, version) VALUES (5, 6, null, 'no status', 0)", phase = Sql.Phase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM multimedia.review WHERE ID = 5", phase = Sql.Phase.AFTER_TEST_METHOD)
    public void converterInvokedOnNullValue() throws Throwable {
        final EntityManager em = provider.em();
        try {
            final List<Review> code = em.createQuery("""
                        select r 
                        from Review r 
                        where r.rating < :code or r.rating is null
                    """, Review.class)
                    .setParameter("code", Rating.ONE)
                    .getResultList();

            Assertions.fail("should faild in the converter");

        } catch (PersistenceException e) {

            assertThat(e.getMessage()).contains("Error attempting to apply AttributeConverter");
            assertThat(e).isNotNull().hasCauseExactlyInstanceOf(IllegalStateException.class);
            assertThat(e.getCause().getMessage()).isEqualTo("invalid status with the code 'null'");

        }  finally {
            em.close();
        }
    }
}
