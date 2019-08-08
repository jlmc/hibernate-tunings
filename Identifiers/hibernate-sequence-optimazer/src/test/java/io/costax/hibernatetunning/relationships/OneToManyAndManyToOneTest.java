package io.costax.hibernatetunning.relationships;

import io.costax.hibernatetunings.entities.project.Issue;
import io.costax.hibernatetunings.entities.project.Project;
import io.costax.rules.EntityManagerProvider;
import org.junit.*;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;

/**
 * 5.2 - one-to-many-and-many-to-one
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OneToManyAndManyToOneTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    /**
     * 1
     */
    @Test
    public void t00_create_a_new_project() {
        Project project = Project.of("soteria");

        provider.beginTransaction();
        provider.em().persist(project);
        provider.commitTransaction();
    }

    /**
     * 2
     */
    @Test
    public void t01_add_and_remove_issue() {
        provider.beginTransaction();

        final EntityManager em = provider.em();

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

        provider.commitTransaction();
    }

    @Test
    public void t02_add_Issue_to_project_and_then_remove() {
        provider.beginTransaction();
        final EntityManager em = provider.em();
        final Project soteria = em.createQuery("from Project p where p.title = :title", Project.class)
                .setParameter("title", "soteria")
                .getSingleResult();

        soteria.addIssue(Issue.of(soteria, "abcde"));
        em.flush();

        em.remove(soteria);
        em.flush();
        
        provider.commitTransaction();
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
    @Test(expected = IllegalStateException.class)
    //@Ignore
    public void t03_should_not_create_project_when_create_issue() {
        Project jcache = Project.of("jcache");

        provider.beginTransaction();
        final Issue todo1 = Issue.of(jcache, "jcache:: todo1");

        try {
            provider.em().persist(todo1);

            provider.em().flush();

            provider.commitTransaction();

            Assert.fail("should faild because jcache is transient reference");
        } catch (IllegalStateException e) {
            // Caused by: java.lang.IllegalStateException: org.hibernate.TransientPropertyValueException: Not-null property references a transient value - transient instance must be saved before current operation : Issue.project -> Project
            provider.rollbackTransaction();

            throw e;
        }
    }

    /**
     * 4
     * update project when create issue
     */
    @Test
    @Ignore
    public void t04_update_project_when_create_issue() {
        Project jcache = Project.of("jcache2");

        provider.beginTransaction();

        final Issue issue = provider.em().createQuery("from Issue i where i.title = :t", Issue.class).setParameter("t", "jcache:: todo1").getSingleResult();

        issue.changeProject(jcache, "jcahes updated");

        //provider.em().persist(todo1);

        provider.commitTransaction();
    }

    @Test
    @Ignore
    public void t05_delete() {
        provider.beginTransaction();
        final Issue issue = provider.em().find(Issue.class, 5L);

        provider.em().remove(issue);

        provider.commitTransaction();
    }

    @Test
    public void t06_delete_all_data() {
        provider.beginTransaction();

        provider.em().createQuery("delete from Issue ").executeUpdate();
        provider.em().createQuery("delete from Project ").executeUpdate();

        provider.commitTransaction();
    }
}
