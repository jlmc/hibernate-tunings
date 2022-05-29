package io.costax.concurrency.pessimistic.books;

import io.costax.concurrency.domain.books.Author;
import io.costax.concurrency.pessimistic.Utils;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

/**
 * Test 3
 * Read lock can block write lock
 */
@JpaTest(persistenceUnit = "it")
public class ConcurrentWriteAndReadTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentWriteAndReadTest.class);

    @JpaContext
    public JpaProvider provider;

    @Test
    public void testConcurrentWriteAndRead() throws InterruptedException {

        EntityManager em1 = provider.em();
        em1.getTransaction().begin();

        LOGGER.info("Try to select Author with write lock in transaction 1");

        // select * from multimedia.author author0_ where author0_.id=? for update
        Author a1 = em1.find(Author.class, 1L, LockModeType.PESSIMISTIC_WRITE);
        LOGGER.info("Update Author in transaction 1");
        a1.setFirstName("changed");

        // read Author entity in a 2nd thread
        Thread t = new Thread(() -> {
            EntityManager em2 = provider.em();
            em2.getTransaction().begin();

            //   // select * from multimedia.author author0_ where author0_.id=? for share
            Author a2 = em2.find(Author.class, 1L, LockModeType.PESSIMISTIC_READ);
            // must wait until any write lock be release to continue the execution

            LOGGER.info("Transaction 2: " + a2);

            //seep(5);


            LOGGER.info("Commit transaction 2");
            em2.getTransaction().commit();
            em2.close();
        });

        t.start();

        LOGGER.info("Sleep for 3 seconds before committing transaction 1");

        Utils.sleepSeconds(2);
        LOGGER.info("Commit transaction 1");

        em1.getTransaction().commit();
        em1.close();

        t.join();
    }


}
