package io.costax.hibernatetunings.cache.queries;

import io.costax.hibernatetunings.entities.project.Project;
import io.costax.rules.Watcher;
import org.hibernate.Session;
import org.hibernate.annotations.QueryHints;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.List;

public class TestQueryCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestQueryCache.class);

    private static EntityManagerFactory emf;

    @Rule
    public Watcher watcher = Watcher.timer(LOGGER);

    @BeforeClass
    public static void initEntityManagerFactory() {
        emf = Persistence.createEntityManagerFactory("it");
    }

    @AfterClass
    public static void closeEntityManagerFactory() {
        emf.close();
    }

    @Test
    public void testQueryCacheAdHocQuery() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Session s = (Session) em.getDelegate();
        Query q = s.createQuery("select p from Project p where id = :id");
        q.setParameter("id", 1L);

        ((org.hibernate.query.Query) q).setCacheable(true);

        final Object firtsExecution = q.getSingleResult();
        final Object secoundExecution = q.getSingleResult();

        em.getTransaction().commit();
        em.close();

        Assert.assertSame(firtsExecution, secoundExecution);
    }

    @Test
    public void testQueryCacheWithTwoTransactions() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        final List<Project> projects1 = em.createQuery("select p from Project p where id = :id", Project.class)
                .setParameter("id", 1L)
                .setHint(QueryHints.CACHEABLE, true)
                .getResultList();

        em.getTransaction().commit();
        em.close();

        em = emf.createEntityManager();
        em.getTransaction().begin();

        final TypedQuery<Project> query2 = em.createQuery("select p from Project p where id = :id", Project.class)
                .setParameter("id", 1L);
        //.setHint(QueryHints.CACHEABLE, true)
        ((org.hibernate.query.Query) query2).setCacheable(true);
        final List<Project> projects2 = query2.getResultList();

        em.getTransaction().commit();
        em.close();

        Assert.assertEquals(projects1.size(), projects2.size());
        Assert.assertEquals(1, projects1.size());
        Assert.assertEquals(projects1.get(0), projects2.get(0));
    }

    @Test
    public void testQueryCacheUpdateEntity() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        final TypedQuery<Project> query = em.createQuery("select p from Project p where id = :id", Project.class)
                .setParameter("id", 1L)
                .setHint(QueryHints.CACHEABLE, true);

        Project project1 = query.getSingleResult();

        project1.setTitle("Effective java Third edition");

        Project project2 = query.getSingleResult();

        em.getTransaction().commit();
        em.close();

        Assert.assertNotNull(project2);
        Assert.assertEquals("Effective java Third edition", project2.getTitle());
    }


    @Test
    public void testQueryCacheWithDifferentParameter() {

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Session s = (Session) em.getDelegate();
        Query q = s.createQuery("select p from Project p where id = :id");
        q.setParameter("id", 1L);
        ((org.hibernate.query.Query) q).setCacheable(true);
        LOGGER.info("{}", ((org.hibernate.query.Query) q).list().get(0));

        s = (Session) em.getDelegate();
        q = s.createQuery("select p from Project p where id = :id");
        q.setParameter("id", 2L);
        ((org.hibernate.query.Query) q).setCacheable(true);
        LOGGER.info("{}", ((org.hibernate.query.Query) q).list().get(0));

        em.getTransaction().commit();
        em.close();
    }

}
