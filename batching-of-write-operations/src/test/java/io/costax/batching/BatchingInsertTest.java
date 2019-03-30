package io.costax.batching;

import io.costax.rules.EntityManagerProvider;
import io.costax.rules.Watcher;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;

/**
 * Test nº 1
 */
public class BatchingInsertTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchingInsertTest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Rule
    public Watcher watcher = Watcher.timer(LOGGER);

    @Test
    public void testInsertActors() {
        EntityManager em = provider.em();
        em.getTransaction().begin();

        for (int i = 1; i <= 20; i++) {
            Author actor = Author.of("First-Name-" + i, "LastName" + i);

            em.persist(actor);

            if (i % 5 == 0) {
                em.flush();
                em.clear();
            }
        }

        em.getTransaction().commit();
        em.close();
    }
}
