package io.costa.hibernatetunings.cache.secoundlevel;

import io.costa.hibernatetunings.cache.Watcher;
import io.costa.hibernatetunings.entities.project.Issue;
import io.costa.hibernatetunings.entities.project.Project;
import org.hamcrest.Matchers;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.stream.Collectors;

public class TestSecoundLevelCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestSecoundLevelCache.class);

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

        Assert.assertNotNull(p1);
        Assert.assertNotNull(p2);
        Assert.assertThat(p1, Matchers.hasProperty("title", Matchers.equalTo("effective-java-3")));
        Assert.assertThat(p2, Matchers.hasProperty("title", Matchers.equalTo("effective-java-3")));
        Assert.assertThat(p2, Matchers.equalTo(p1));
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