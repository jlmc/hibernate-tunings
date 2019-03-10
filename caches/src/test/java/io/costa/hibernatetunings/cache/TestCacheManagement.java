package io.costa.hibernatetunings.cache;

import io.costa.hibernatetunings.cache.secoundlevel.TestRetrieveAndStoreMode;
import io.costa.hibernatetunings.entities.project.Issue;
import io.costa.hibernatetunings.entities.project.Project;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class TestCacheManagement {

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
    public void test2ndLevelCacheEviction() {

        LOGGER.info("populate cache");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        em.find(Project.class, 1L);
        em.find(Project.class, 2L);
        em.find(Issue.class, 1L);

        em.getTransaction().commit();
        em.close();

        // 2nd session
        em = emf.createEntityManager();
        em.getTransaction().begin();

        Cache cache = em.getEntityManagerFactory().getCache();
        logCachedObjects(cache);

        LOGGER.info("Evict Project 1");
        cache.evict(Project.class, 1L);
        logCachedObjects(cache);

        LOGGER.info("Evict all Project");
        cache.evict(Project.class);
        logCachedObjects(cache);

        LOGGER.info("Evict all");
        cache.evictAll();
        logCachedObjects(cache);

        em.getTransaction().commit();
        em.close();
    }

    private void logCachedObjects(Cache cache) {
        LOGGER.info("Cache contains Project 1? " + cache.contains(Project.class, 1L));
        LOGGER.info("Cache contains Project 2? " + cache.contains(Project.class, 2L));
        LOGGER.info("Cache contains Issue 1? " + cache.contains(Issue.class, 1L));
    }
}
