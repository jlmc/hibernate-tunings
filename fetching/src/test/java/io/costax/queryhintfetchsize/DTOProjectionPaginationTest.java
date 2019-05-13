package io.costax.queryhintfetchsize;

import io.costax.model.Project;
import io.costax.model.ProjectSummary;
import io.costax.rules.EntityManagerProvider;
import org.hamcrest.Matchers;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * ORDER BY
 * Without the ORDER BY clause, the order of rows in a result set is not deterministic. However,
 * in the pagination use case, the fetched record order need to be preserved whenever moving
 * from one page to another. According to the SQL standard, only the ORDER BY clause can
 * guarantee a deterministic result set order because records are sorted after being extracted.
 * In the context of pagination, the ORDER BY clause needs to be applied on a column
 * or a set of columns that are guarded by a unique constraint.
 */
public class DTOProjectionPaginationTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void projection_with_jpql() {
        List<ProjectSummary> pageOfSecondFive = provider.em().createQuery("select new io.costax.model.ProjectSummary(p.id, p.title) from Project p order by p.id desc ", ProjectSummary.class)
                .setMaxResults(5)
                .setFirstResult(5)
                .getResultList();

        Assert.assertThat(pageOfSecondFive, Matchers.hasSize(5));
        Assert.assertThat(pageOfSecondFive.get(0).getId(), Matchers.is(50L));
        Assert.assertThat(pageOfSecondFive.get(4).getId(), Matchers.is(46L));
    }

    @Test
    public void projection_with_criteria() {
        final EntityManager em = provider.em();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProjectSummary> cq = cb.createQuery(ProjectSummary.class);

        final Root<Project> p = cq.from(Project.class);

        final CriteriaQuery<ProjectSummary> projectSummaryCriteriaQuery = cq.select(cb.construct(ProjectSummary.class, p.get("id"), p.get("title")))
                .orderBy(cb.desc(p.get("id")));

        final List<ProjectSummary> pageOfSecondFive = em.createQuery(projectSummaryCriteriaQuery)
                .setMaxResults(5)
                .setFirstResult(5)
                .getResultList();

        Assert.assertThat(pageOfSecondFive, Matchers.hasSize(5));
        Assert.assertThat(pageOfSecondFive.get(0).getId(), Matchers.is(50L));
        Assert.assertThat(pageOfSecondFive.get(4).getId(), Matchers.is(46L));
    }

    @Test
    public void projection_with_native() {
        final EntityManager em = provider.em();
        List<ProjectSummary> pageOfSecondsFive = em.createNativeQuery("select p.id as id, p.title as title from project p order by p.id desc", "ProjectSummaryMapper")
                .setMaxResults(5)
                .setFirstResult(5)
                .getResultList();

        Assert.assertThat(pageOfSecondsFive, Matchers.hasSize(5));
        Assert.assertThat(pageOfSecondsFive.get(0).getId(), Matchers.is(50L));
        Assert.assertThat(pageOfSecondsFive.get(4).getId(), Matchers.is(46L));
    }

    @Test
    public void projection_with_named_native() {
        final EntityManager em = provider.em();
        List<ProjectSummary> pageOfSecondFive = em.createNamedQuery("ProjectSummaryQuery", ProjectSummary.class)
                .setMaxResults(5)
                .setFirstResult(5)
                .getResultList();

        Assert.assertThat(pageOfSecondFive, Matchers.hasSize(5));
        Assert.assertThat(pageOfSecondFive.get(0).getId(), Matchers.is(50L));
        Assert.assertThat(pageOfSecondFive.get(4).getId(), Matchers.is(46L));
    }

    @Test
    public void projection_with_native_using_AliasToBeanResultTransformer() {
        /**
         * A much simpler alternative is to use the Hibernate-native API which allows transforming the
         * ResultSet to a DTO through Java Reflection:
         */
        final Session session = provider.em().unwrap(Session.class);

        List<ProjectSummary> pageOfSecondFive = session.createSQLQuery(
                "select p.id as id, p.title as title from project p order by p.id desc")
                .setFirstResult(5)
                .setMaxResults(5)
                .setResultTransformer(
                        new AliasToBeanResultTransformer(ProjectSummary.class))
                .list();

        Assert.assertThat(pageOfSecondFive, Matchers.hasSize(5));
        Assert.assertThat(pageOfSecondFive.get(0).getId(), Matchers.is(50L));
        Assert.assertThat(pageOfSecondFive.get(4).getId(), Matchers.is(46L));

        /**
         * Although JPA 2.1 supports Constructor Expressions for JPQL queries as previously
         * illustrated, there is no such alternative for native SQL queries.
         * Fortunately, Hibernate has long been offering this feature through the
         * ResultTransformer mechanism which not only provides a way to return DTO projections,
         * but it allows to customize the result set transformation, like when needing to build
         * an hierarchical DTO structure.
         */
    }
}
