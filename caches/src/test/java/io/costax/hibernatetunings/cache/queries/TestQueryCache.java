package io.costax.hibernatetunings.cache.queries;

import io.costax.hibernatetunings.entities.project.Project;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.annotations.QueryHints;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@JpaTest(persistenceUnit = "it")
public class TestQueryCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestQueryCache.class);

    @PersistenceUnit
    public EntityManagerFactory emf;

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


        Assertions.assertTrue(firtsExecution == secoundExecution);
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

        Assertions.assertEquals(projects1.size(), projects2.size());
        Assertions.assertEquals(1, projects1.size());
        Assertions.assertEquals(projects1.get(0), projects2.get(0));
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

        Assertions.assertNotNull(project2);
        Assertions.assertEquals("Effective java Third edition", project2.getTitle());
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
