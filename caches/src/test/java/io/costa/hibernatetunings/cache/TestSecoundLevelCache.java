package io.costa.hibernatetunings.cache;

import io.costa.hibernatetunings.entities.project.Issue;
import io.costa.hibernatetunings.entities.project.Project;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.stream.Collectors;

public class TestSecoundLevelCache {

    public static final Logger LOGGER = LoggerFactory.getLogger(TestSecoundLevelCache.class);

    @Rule
    public Watcher watcher = Watcher.timer(LOGGER);

    private static EntityManagerFactory emf;

    @BeforeClass
    public static void initEntityManagerFactory() {
        emf = Persistence.createEntityManagerFactory("it");
    }

    @AfterClass
    public static void closeEntityManagerFactory() {
        emf.close();
    }


    private Long projectId;


    //@Before
    //@Test
    @Ignore
    public void populate() {
        final EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        final Project p1 = Project.of("\"TestSecoundLevelCache\"");

        em.persist(p1);
        em.flush();

        projectId = p1.getId();

        em.getTransaction().commit();

        em.close();
    }


    @Test
    public void test2TX() {

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Project p1 = em.find(Project.class, 331L);

        em.getTransaction().commit();
        em.close();


        em = emf.createEntityManager();
        em.getTransaction().begin();

        Project p2 = em.find(Project.class, 331L);

        em.getTransaction().commit();
        em.close();
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
        LOGGER.info("Project " + a.getTitle() + " contains: "
                + a.getIssues().stream().map(Issue::getTitle).collect(Collectors.joining(", ")));
    }

}