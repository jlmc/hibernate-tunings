package io.costax.queryhintfetchsize;

import io.costax.model.Project;
import io.costax.model.ProjectSummary;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.jpa.QueryHints;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
@JpaTest(persistenceUnit = "it")
public class DTOProjectionPaginationTest {

    private static final int PAGE_START = 5;
    private static final int PAGE_SIZE = 5;

    @PersistenceContext
    public EntityManager em;

    @Test
    public void projection_with_jpql() {
        List<ProjectSummary> pageOfSecondFive = em
                .createQuery("""
                        select new io.costax.model.ProjectSummary(p.id, p.title) 
                        from Project p 
                        order by p.id desc
                        """, ProjectSummary.class)
                .setMaxResults(PAGE_SIZE)
                .setFirstResult(PAGE_START)
                .setHint(QueryHints.HINT_FETCH_SIZE, PAGE_SIZE)
                .getResultList();

        Assertions.assertEquals(5, pageOfSecondFive.size());
        Assertions.assertEquals(50L, pageOfSecondFive.get(0).getId());
        Assertions.assertEquals(46L, pageOfSecondFive.get(4).getId());
    }

    @Test
    public void projection_with_criteria() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProjectSummary> cq = cb.createQuery(ProjectSummary.class);

        final Root<Project> p = cq.from(Project.class);

        final CriteriaQuery<ProjectSummary> projectSummaryCriteriaQuery = cq.select(cb.construct(ProjectSummary.class, p.get("id"), p.get("title")))
                .orderBy(cb.desc(p.get("id")));

        final List<ProjectSummary> pageOfSecondFive = em.createQuery(projectSummaryCriteriaQuery)
                .setMaxResults(PAGE_SIZE)
                .setFirstResult(PAGE_START)
                .setHint(QueryHints.HINT_FETCH_SIZE, PAGE_SIZE)
                .getResultList();

        Assertions.assertEquals(5, pageOfSecondFive.size());
        Assertions.assertEquals(50L, pageOfSecondFive.get(0).getId());
        Assertions.assertEquals(46L, pageOfSecondFive.get(4).getId());
    }

    @Test
    public void projection_with_native() {
        List<ProjectSummary> pageOfSecondFive =
                em.createNativeQuery(
                        """ 
                                select p.id as id, p.title as title 
                                from project p 
                                order by p.id desc
                                 """,
                        "ProjectSummaryMapper")
                        .setMaxResults(PAGE_SIZE)
                        .setFirstResult(PAGE_START)
                        .setHint(QueryHints.HINT_FETCH_SIZE, PAGE_SIZE)
                        .getResultList();

        Assertions.assertEquals(5, pageOfSecondFive.size());
        Assertions.assertEquals(50L, pageOfSecondFive.get(0).getId());
        Assertions.assertEquals(46L, pageOfSecondFive.get(4).getId());
    }

    @Test
    public void projection_with_named_native() {
        List<ProjectSummary> pageOfSecondFive =
                em.createNamedQuery("ProjectSummaryQuery", ProjectSummary.class)
                        .setMaxResults(PAGE_SIZE)
                        .setFirstResult(PAGE_START)
                        .setHint(QueryHints.HINT_FETCH_SIZE, PAGE_SIZE)
                        .getResultList();

        Assertions.assertEquals(5, pageOfSecondFive.size());
        Assertions.assertEquals(50L, pageOfSecondFive.get(0).getId());
        Assertions.assertEquals(46L, pageOfSecondFive.get(4).getId());
    }

    @Test
    public void projection_with_native_using_AliasToBeanResultTransformer() {
        /*
         * A much simpler alternative is to use the Hibernate-native API which allows transforming the
         * ResultSet to a DTO through Java Reflection:
         */
        final Session session = em.unwrap(Session.class);

        List<ProjectSummary> pageOfSecondFive =
                session.createQuery(
                        """
                                select p.id as id, p.title as title from project p order by p.id desc
                                """)
                        .setMaxResults(PAGE_SIZE)
                        .setFirstResult(PAGE_START)
                        .setHint(QueryHints.HINT_FETCH_SIZE, PAGE_SIZE)
                        .setResultTransformer(
                                new AliasToBeanResultTransformer(ProjectSummary.class))
                        .list();

        Assertions.assertEquals(5, pageOfSecondFive.size());
        Assertions.assertEquals(50L, pageOfSecondFive.get(0).getId());
        Assertions.assertEquals(46L, pageOfSecondFive.get(4).getId());

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
