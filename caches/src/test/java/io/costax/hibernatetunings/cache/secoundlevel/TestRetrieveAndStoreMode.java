package io.costax.hibernatetunings.cache.secoundlevel;

import io.costax.hibernatetunings.entities.project.Project;
import io.costax.rules.Watcher;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

public class TestRetrieveAndStoreMode {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRetrieveAndStoreMode.class);

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
    public void testRetrieveMode() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Project a = em.find(Project.class, 1L);
        LOGGER.info("---- {} ", a);

        em.getTransaction().commit();
        em.close();

        // 2nd session
        LOGGER.info("2nd session - CacheRetrieveMode = BYPASS");
        em = emf.createEntityManager();
        em.getTransaction().begin();

        // BYPASS the cache and read the author from the database directly
        Map<String, Object> props = new HashMap<>();

        props.put("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);
        a = em.find(Project.class, 1L, props);
        LOGGER.info("---- {} ", a);

        em.getTransaction().commit();
        em.close();

        // 3rd session
        LOGGER.info("2nd session - CacheRetrieveMode = USE");
        em = emf.createEntityManager();
        em.getTransaction().begin();

        // Read the author from the cache
        props = new HashMap<String, Object>();
        props.put("javax.persistence.cache.retrieveMode", CacheRetrieveMode.USE);
        a = em.find(Project.class, 1L, props);
        LOGGER.info("---- {} ", a);

        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testStoreMode() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // BYPASS: update entities in the cache but don't add new ones
        em.setProperty("javax.persistence.cache.storeMode", CacheStoreMode.BYPASS);

        Project a = Project.of("clean-architecture");
        em.persist(a);
        em.flush();

        final Long id = a.getId();

        em.getTransaction().commit();
        em.close();

        LOGGER.info("2nd session - StoreMode = USE");

        em = emf.createEntityManager();
        em.getTransaction().begin();

        // USE: add and update entities in the cache
        em.setProperty("javax.persistence.cache.storeMode", CacheStoreMode.USE);

        a = em.find(Project.class, id);
        LOGGER.info("---- {} ", a);

        em.getTransaction().commit();
        em.close();

        LOGGER.info("2nd session - StoreMode = REFRESH");

        em = emf.createEntityManager();
        em.getTransaction().begin();

        // REFRESH: get entities from the database and update them in the cache
        em.setProperty("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH);

        a = em.find(Project.class, id);
        LOGGER.info("---- {} ", a);

        em.getTransaction().commit();
        em.close();
    }
}
