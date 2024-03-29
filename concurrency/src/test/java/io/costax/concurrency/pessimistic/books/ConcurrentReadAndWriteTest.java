package io.costax.concurrency.pessimistic.books;

import io.costax.concurrency.domain.books.Author;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

/**
 * Test 2
 * Read lock can block write lock
 */
@JpaTest(persistenceUnit = "it")
public class ConcurrentReadAndWriteTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentReadAndWriteTest.class);

    @JpaContext
    public JpaProvider provider;

    @Test
    public void testConcurrentReadAndWrite() throws InterruptedException {

        EntityManager em1 = provider.em();
        em1.getTransaction().begin();

        Author a1 = em1.find(Author.class, 1L, LockModeType.PESSIMISTIC_READ);
        LOGGER.info("Transaction 1: " + a1);

        // perform update in a 2nd thread
        // we are using other thread because if we acquire the a PESSIMISTIC_WRITE on the current transaction
        // after having a PESSIMISTIC_READ the current thread will be stacked and deadlock
        Thread t = new Thread(() -> {
            EntityManager em2 = provider.em();
            em2.getTransaction().begin();

            LOGGER.info("Try to select Author with write lock in transaction 2");
            Author a2 = em2.find(Author.class, 1L, LockModeType.PESSIMISTIC_WRITE);

            seep(3);

            LOGGER.info("Update Author in transaction 2");
            a2.setFirstName("changed");

            LOGGER.info("Commit transaction 2");
            em2.getTransaction().commit();
            em2.close();
        });
        t.start();

        LOGGER.info("Sleep for 3 seconds before committing transaction 1");

        seep(3);

        LOGGER.info("Commit transaction 1");
        em1.getTransaction().commit();
        em1.close();

        LOGGER.info("*** on Now the Transaction1 is finished, and only alter this the executions of the Transaction-2 can be done ");

        t.join();
    }

    private void seep(long sec) {
        try {
            Thread.sleep(sec * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
