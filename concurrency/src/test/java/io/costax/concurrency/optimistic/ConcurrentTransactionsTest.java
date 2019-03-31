package io.costax.concurrency.optimistic;

import batching.Author;
import io.costax.rules.EntityManagerProvider;
import io.costax.rules.Watcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;

public class ConcurrentTransactionsTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptimisticLockTest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Rule
    public Watcher watcher = Watcher.timer(LOGGER);

    @Test
    public void testConcurrentUpdate() {

        final EntityManager em1 = provider.createdEntityManagerUnRuled();

        em1.getTransaction().begin();

        EntityManager em2 = provider.createdEntityManagerUnRuled();
        em2.getTransaction().begin();

        Author a1 = em1.find(Author.class, 1L);
        a1.setFirstName("changed");

        Author a2 = em2.find(Author.class, 1L);
        a2.setLastName("something else");

        em1.getTransaction().commit();
        em1.close();

        try {
            em2.getTransaction().commit();

            Assert.fail("RollbackExecption expected");
        } catch (RollbackException e) {
            if (e.getCause() instanceof OptimisticLockException) {
                LOGGER.info("{}", e.getCause());
            } else {
                Assert.fail("OptimisticLockException expected");
            }
        }

        em2.close();
    }
}
