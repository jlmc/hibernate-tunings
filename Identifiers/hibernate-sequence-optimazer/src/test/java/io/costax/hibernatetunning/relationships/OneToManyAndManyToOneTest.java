package io.costax.hibernatetunning.relationships;

import io.costa.hibernatetunings.entities.project.Issue;
import io.costa.hibernatetunings.entities.project.Project;
import io.costax.rules.EntityManagerProvider;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;

/**
 * 5.2 - one-to-many-and-many-to-one
 */
public class OneToManyAndManyToOneTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    /**
     * 1
     */
    @Test
    public void createANewProject() {
        Project project = Project.of("soteria");

        provider.beginTransaction();
        provider.em().persist(project);
        provider.commitTransaction();
    }

    /**
     * 2
     */
    @Test
    public void addAndRemoveIssue() {
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
    public void name() {
        provider.beginTransaction();
        final EntityManager em = provider.em();
        final Project soteria = em.createQuery("from Project p where p.title = :title", Project.class)
                .setParameter("title", "soteria")
                .getSingleResult();

        soteria.addIssue(Issue.of(soteria, "abcde"));

        em.remove(soteria);

        provider.commitTransaction();
    }

    /**
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
    @Test
    @Ignore
    public void createProjectWhenCreateIssue() {

        Project jcache = Project.of("jcache");

        provider.beginTransaction();
        final Issue todo1 = Issue.of(jcache, "jcache:: todo1");

        provider.em().persist(todo1);

        provider.commitTransaction();
    }

    /**
     * 4
     * update project when create issue
     */
    @Test
    @Ignore
    public void updateProjectWhenCreateIssue() {

        Project jcache = Project.of("jcache2");

        provider.beginTransaction();

        final Issue issue = provider.em().createQuery("from Issue i where i.title = :t", Issue.class).setParameter("t", "jcache:: todo1").getSingleResult();

        issue.changeProject(jcache, "jcahes updated");

        //provider.em().persist(todo1);

        provider.commitTransaction();
    }

    @Test
    @Ignore
    public void delete() {
        provider.beginTransaction();
        final Issue issue = provider.em().find(Issue.class, 5L);

        provider.em().remove(issue);

        provider.commitTransaction();
    }

}
