package io.costax.hibernatetunning.relationships;

import io.costax.hibernatetunings.entities.project.Issue;
import io.costax.hibernatetunings.entities.project.Project;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.TransientPropertyValueException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 5.2 - one-to-many-and-many-to-one
 */
@JpaTest(persistenceUnit = "it")
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class OneToManyAndManyToOneTest {

    @PersistenceContext
    public EntityManager em;

    /**
     * 1
     */
    @Test
    public void t00_create_a_new_project() {
        Project project = Project.of("soteria");

        em.getTransaction().begin();
        em.persist(project);
        em.getTransaction().commit();
    }

    /**
     * 2
     */
    @Test
    public void t01_add_and_remove_issue() {
        em.getTransaction().begin();

        final Project soteria = em.createQuery("from Project p where p.title = :title", Project.class)
                .setParameter("title", "soteria")
                .getSingleResult();

        //final Issue todo4 = Issue.of(null, "Todo3");
        final Issue todo1 = Issue.of(soteria, "Todo1");
        final Issue todo2 = Issue.of(soteria, "Todo2");
        final Issue todo3 = Issue.of(soteria, "Todo3");

        //em.persist(todo4);
        em.persist(todo1);
        em.persist(todo2);
        em.persist(todo3);

        em.getTransaction().commit();
    }

    @Test
    public void t02_add_Issue_to_project_and_then_remove() {
        em.getTransaction().begin();
        final Project soteria = em.createQuery("from Project p where p.title = :title", Project.class)
                .setParameter("title", "soteria")
                .getSingleResult();

        soteria.addIssue(Issue.of(soteria, "abcde"));
        em.flush();

        em.remove(soteria);
        em.flush();

        em.getTransaction().commit();
    }

    /*
     * The next are only valid for the following relationship configuraton
     * @Entity
     * @Table(name = "issue")
     * public class Issue extends BaseEntity {
     *
     *     @ManyToOne(cascade = {PERSIST, MERGE, REMOVE})
     *     @JoinColumn(name = "project_id", nullable = false)
     *     private Project project;
     */


    /**
     * 3
     * create project when create issue
     */
    @Test//(expected = IllegalStateException.class)
    //@Ignore
    public void t03_should_not_create_project_when_create_issue() {
        Project jcache = Project.of("jcache");


        final IllegalStateException illegalStateException = Assertions.assertThrows(IllegalStateException.class, () -> {

            em.getTransaction().begin();
            final Issue todo1 = Issue.of(jcache, "jcache:: todo1");

            try {
                em.persist(todo1);

                em.flush();

                em.getTransaction().commit();

                fail("should fail because jcache is transient reference");
            } catch (IllegalStateException e) {
                // Caused by: java.lang.IllegalStateException: org.hibernate.TransientPropertyValueException: Not-null property references a transient value - transient instance must be saved before current operation : Issue.project -> Project
                em.getTransaction().rollback();

                throw e;
            }

        });

        assertNotNull(illegalStateException.getCause());
        assertTrue( org.hibernate.TransientPropertyValueException.class
                .isAssignableFrom(illegalStateException.getCause().getClass()));
        assertTrue(illegalStateException.getMessage().contains("Not-null property references a transient value - transient instance must be saved before current operation"));
        org.hibernate.TransientPropertyValueException cause = (TransientPropertyValueException) illegalStateException.getCause();
        assertEquals("project", cause.getPropertyName());
        assertEquals(Project.class.getName(), cause.getTransientEntityName());
        assertEquals(Issue.class.getName(), cause.getPropertyOwnerEntityName());
        assertTrue( cause.getMessage().contains("Not-null property references a transient value - transient instance must be saved before current operation"));
    }

    /**
     * 4
     * update project when create issue
     */
    @Test
    @Disabled
    public void t04_update_project_when_create_issue() {
        Project jcache = Project.of("jcache2");

        em.getTransaction().begin();

        final Issue issue = em.createQuery("from Issue i where i.title = :t", Issue.class)
                .setParameter("t", "jcache:: todo1")
                .getSingleResult();

        issue.changeProject(jcache, "jcahes updated");

        em.getTransaction().commit();
    }

    @Test
    @Disabled
    public void t05_delete() {
        em.getTransaction().begin();
        final Issue issue = em.find(Issue.class, 5L);

        em.remove(issue);

        em.getTransaction().commit();
    }

    @Test
    public void t06_delete_all_data() {
        em.getTransaction().begin();

        em.createQuery("delete from Issue ").executeUpdate();
        em.createQuery("delete from Project ").executeUpdate();

        em.getTransaction().rollback();
    }
}
