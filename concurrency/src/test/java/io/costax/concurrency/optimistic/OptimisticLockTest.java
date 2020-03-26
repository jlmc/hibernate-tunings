package io.costax.concurrency.optimistic;

import io.costax.concurrency.domain.books.Author;
import io.costax.rules.EntityManagerProvider;
import io.costax.rules.Watcher;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 1
 */
public class OptimisticLockTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptimisticLockTest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Rule
    public Watcher watcher = Watcher.timer(LOGGER);

    @Test
    public void testUpdate() {
        provider.doInTx(em -> {
            Author a = em.find(Author.class, 1L);
            a.setFirstName("Saramago");
        });
    }
}
