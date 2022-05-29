package io.costax.concurrency.pessimistic.books;

import io.costax.concurrency.domain.books.Author;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.LockTimeoutException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test 4
 */
@JpaTest(persistenceUnit = "it")
public class TimeoutTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeoutTest.class);

    @JpaContext
    public JpaProvider provider;

    @Test
    public void testZeroTimeout() {

        EntityManager em1 = provider.em();
        em1.getTransaction().begin();

        EntityManager em2 = provider.em();
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
            hints.put("jakarta.persistence.lock.timeout", 0);

            Author a2 = em2.find(Author.class, 1L, LockModeType.PESSIMISTIC_WRITE, hints);

            fail("LockTimeoutException expected");

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
    @Disabled("Not supported by Postgres SQL. Postgres do not support timeout greater than Zero!!!")
    public void testMoreThanZeroTimeout() {
        final Long authorId = 1L;

        EntityManager em1 = provider.em();
        em1.getTransaction().begin();

        EntityManager em2 = provider.em();
        em2.getTransaction().begin();

        {

            LOGGER.info("Transaction-1 => Select/Fetch record with ID '{}' with LockModeType.PESSIMISTIC_READ!!!", authorId);

            Author a1 = em1.find(Author.class, authorId, LockModeType.PESSIMISTIC_READ);
            LOGGER.info("Transaction 1: " + a1);
        }

        {
            // try to acquire write lock with NOWAIT clause
            LOGGER.info("Transaction-2 => Try to select record with ID '{}' that is Locked for any Reading!!!", authorId);

            try {

                Map<String, Object> hints = new HashMap<>();
                hints.put("jakarta.persistence.lock.timeout", 5);

                @SuppressWarnings("unused")
                Author a2 = em2.find(Author.class, 1L, LockModeType.PESSIMISTIC_WRITE, hints);

                fail("LockTimeoutException expected");

            } catch (LockTimeoutException e) {
                LOGGER.info("caching {} ", e.getMessage(), e);
                em2.close();
            }
        }

        LOGGER.info("Commit transaction 1");
        em1.getTransaction().commit();
        em1.close();
    }
}
