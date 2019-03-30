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
 * Test nº 5
 */
public class BatchingDeleteEntityWithBatchTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchingDeleteEntityWithBatchTest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Rule
    public Watcher watcher = Watcher.timer(LOGGER);

    @Test
    public void testDeleteActorsWithBatch() {

        EntityManager em = provider.em();
        em.getTransaction().begin();

        em.createQuery("DELETE Review r")
                .executeUpdate();

        em.createQuery("DELETE Book b")
                .executeUpdate();

        List<Author> authors = em.createQuery("SELECT a FROM Author a LEFT JOIN FETCH a.books", Author.class).getResultList();

        for (Author a : authors) {
            em.remove(a);
        }

        em.getTransaction().commit();
        em.close();
    }
}
