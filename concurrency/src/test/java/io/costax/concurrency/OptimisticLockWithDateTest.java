package io.costax.concurrency;

import batching.Publisher;
import io.costax.rules.EntityManagerProvider;
import io.costax.rules.Watcher;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test nº 3
 */
public class OptimisticLockWithDateTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptimisticLockWithDateTest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Rule
    public Watcher watcher = Watcher.timer(LOGGER);

    @Test
    public void optimisticLockWithDateTest() {
        provider.doInTx(em -> {
            final Publisher publisher = em.find(Publisher.class, 1);
            publisher.setName(publisher.getName() + " -- updated");
        });
    }
}
