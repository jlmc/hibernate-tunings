package io.costax.batching;

import io.costax.rules.EntityManagerProvider;
import io.costax.rules.Watcher;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Test nº 4
 */
public class BatchingDeleteEntityWithoutBatchTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchingDeleteEntityWithoutBatchTest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Rule
    public Watcher watcher = Watcher.timer(LOGGER);

    @Test
    public void testDeleteActorsWithoutBatch() {

        EntityManager em = provider.em();
        em.getTransaction().begin();

        List<Author> authors = em.createQuery("SELECT a FROM Author a", Author.class).getResultList();

        for (Author a : authors) {
            em.remove(a);
        }

        em.getTransaction().commit();
        em.close();
    }

}
