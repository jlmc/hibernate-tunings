package io.costax.hibernatetunings.bulkoperations;

import io.costax.hibernatetunings.entities.project.Issue;
import io.costax.hibernatetunings.entities.project.Project;
import io.costax.rules.Watcher;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.stream.IntStream;

public class TestBatchOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestBatchOperations.class);

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
    public void testInsertProjects() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        for (int i = 1; i <= 10; i++) {
            Project a = Project.of("make it with oop " + i);
            em.persist(a);

            if (i % 5 == 0) {
                em.flush();
                em.clear();
            }
        }

        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testInsertProjectsWithIssues() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        for (int i = 0; i < 10; i++) {

            Project a = Project.of("bulk operation make it with oop " + i);
            em.persist(a);

            IntStream.rangeClosed(1, 3)
                    .mapToObj(index -> Issue.of(a, a.getTitle() + " -- issue (" + index + ")"))
                    .forEach(issue -> {
                        a.addIssue(issue);
                        em.persist(issue);
                    });

        }

        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testUpdateProjectsAndIssues() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        List<Project> projects = em.createQuery("SELECT a FROM Project a JOIN FETCH a.issues ", Project.class).getResultList();

        for (Project a : projects) {
            a.setTitle(a.getTitle() + " - updated");
            a.getIssues().forEach(b -> b.setTitle(b.getTitle() + " - updated"));
        }

        em.getTransaction().commit();
        em.close();
    }

}
