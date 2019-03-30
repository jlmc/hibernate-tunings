package io.costax.batching;

import io.costax.rules.EntityManagerProvider;
import io.costax.rules.Watcher;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;

/**
 * Test nº 2
 */
public class BatchingInsertEntityWithDependentsTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchingInsertEntityWithDependentsTest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Rule
    public Watcher watcher = Watcher.timer(LOGGER);

    @Test
    public void testInsertActorsSerie() {
        EntityManager em = provider.em();
        em.getTransaction().begin();

        for (int i = 0; i < 10; i++) {
            Author actor = Author.of("First-Name-" + i, "LastName" + i);

            em.persist(actor);

            io.costax.batching.Book b = Book.of("Title-" + i, "Description-" + 1);
            b.addActor(actor);

            em.persist(b);
        }

        em.getTransaction().commit();
        em.close();
    }
}
