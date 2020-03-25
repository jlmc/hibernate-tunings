package io.costax.concurrency.pessimistic.books;

import io.costax.concurrency.domain.books.Author;
import io.costax.rules.EntityManagerProvider;
import io.costax.rules.Watcher;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.LockTimeoutException;
import java.util.HashMap;
import java.util.Map;

/**
 * Test 4
 */
public class TimeoutTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeoutTest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Rule
    public Watcher watcher = Watcher.timer(LOGGER);

    @Test
    public void testZeroTimeout() {

        EntityManager em1 = provider.createdEntityManagerUnRuled();
        em1.getTransaction().begin();

        EntityManager em2 = provider.createdEntityManagerUnRuled();
        em2.getTransaction().begin();

        Author a1 = em1.find(Author.class, 1L, LockModeType.PESSIMISTIC_READ);
        LOGGER.info("Transaction 1: " + a1);

        // try to acquire write lock with NOWAIT clause
        LOGGER.info("Try to select Author with write lock in transaction 2");


        try {


            /*
             * select * from multimedia.author author0_ where author0_.id=? for update nowait
             */
            Map<String, Object> hints = new HashMap<>();
            hints.put("javax.persistence.lock.timeout", 0);

            Author a2 = em2.find(Author.class, 1L, LockModeType.PESSIMISTIC_WRITE, hints);

            Assert.fail("LockTimeoutException expected");
        } catch (LockTimeoutException e) {
            LOGGER.info("caching {} ", e.getMessage(), e);
            em2.close();
        }

        LOGGER.info("Commit transaction 1");
        em1.getTransaction().commit();
        em1.close();
    }

    /**
     * Not supported by Postgres SQL
     */
    @Test
    @Ignore
    public void testMoreThanZeroTimeout() {

        EntityManager em1 = provider.createdEntityManagerUnRuled();
        em1.getTransaction().begin();

        EntityManager em2 = provider.createdEntityManagerUnRuled();
        em2.getTransaction().begin();

        Author a1 = em1.find(Author.class, 1L, LockModeType.PESSIMISTIC_READ);
        LOGGER.info("Transaction 1: " + a1);

        // try to acquire write lock with NOWAIT clause
        LOGGER.info("Try to select Author with write lock in transaction 2");

        try {

            Map<String, Object> hints = new HashMap<>();
            hints.put("javax.persistence.lock.timeout", 5);

            @SuppressWarnings("unused")
            Author a2 = em2.find(Author.class, 1L, LockModeType.PESSIMISTIC_WRITE, hints);

            Assert.fail("LockTimeoutException expected");
        } catch (LockTimeoutException e) {
            LOGGER.info("caching {} ", e.getMessage(), e);
            em2.close();
        }

        LOGGER.info("Commit transaction 1");
        em1.getTransaction().commit();
        em1.close();
    }
}
