package io.costax.hibernatetunings.cache.secoundlevel;

import io.costax.hibernatetunings.entities.project.Issue;
import io.costax.hibernatetunings.entities.project.Project;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import jakarta.persistence.Cache;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JpaTest(persistenceUnit = "it")
public class TestCacheManagement {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRetrieveAndStoreMode.class);

    @PersistenceUnit
    private EntityManagerFactory emf;

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
