package io.costax.hibernatetunings.cache.secoundlevel;

import io.costax.hibernatetunings.entities.project.Issue;
import io.costax.hibernatetunings.entities.project.Project;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.stream.Collectors;

@JpaTest(persistenceUnit = "it")
public class TestSecondLevelCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestSecondLevelCache.class);

    @PersistenceUnit
    private EntityManagerFactory emf;

    @Test
    public void test2TX() {
        // This example is using transactions, but is not mandatory

        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        Project p1 = em.find(Project.class, 1L);


        em.getTransaction().commit();
        em.close();


        em = emf.createEntityManager();
        em.getTransaction().begin();

        Project p2 = em.find(Project.class, 1L);

        em.getTransaction().commit();
        em.close();

        Assertions.assertNotNull(p1);
        Assertions.assertNotNull(p2);
        Assertions.assertEquals("effective-java-3", p1.getTitle());
        Assertions.assertEquals("effective-java-3", p2.getTitle());
        Assertions.assertEquals(p2, p1);
    }

    @Test
    public void testRelationshipCaching() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // get project and his issues
        Project a = em.find(Project.class, 1L);
        writeMessage(a);

        em.getTransaction().commit();
        em.close();

        // 2nd session
        em = emf.createEntityManager();
        em.getTransaction().begin();

        // get project and his issues
        a = em.find(Project.class, 1L);

        writeMessage(a);

        em.getTransaction().commit();
        em.close();
    }

    private void writeMessage(Project a) {
        LOGGER.trace("***** Project " + a.getTitle() + " contains: "
                + a.getIssues().stream().map(Issue::getTitle).collect(Collectors.joining(", ")));
    }

}