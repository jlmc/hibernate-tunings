package io.costax.hibernatetunings.bulkoperations;

import io.costax.hibernatetunings.entities.project.Issue;
import io.costax.hibernatetunings.entities.project.Project;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import org.junit.jupiter.api.Test;

import jakarta.persistence.*;
import java.util.List;
import java.util.stream.IntStream;

@JpaTest(persistenceUnit = "it")
public class BatchOperationsTest {

    @PersistenceUnit
    private EntityManagerFactory emf;

    @Test
    public void testInsertProjects() {
        EntityManager em = createEntityManager();
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

    private EntityManager createEntityManager() {
        return emf.createEntityManager();
    }

    @Test
    public void testInsertProjectsWithIssues() {
        EntityManager em = this.createEntityManager();
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
        EntityManager em = createEntityManager();
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
