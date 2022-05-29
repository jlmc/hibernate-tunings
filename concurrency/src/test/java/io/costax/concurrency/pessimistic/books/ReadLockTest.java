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

@JpaTest(persistenceUnit = "it")
public class ReadLockTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadLockTest.class);

    @JpaContext
    public JpaProvider provider;

    @Test
    public void testReadLock() {
        EntityManager em1 = provider.em();
        em1.getTransaction().begin();

        EntityManager em2 = provider.em();
        em2.getTransaction().begin();


        // Hibernate acquire a READ_LOCK for this row and keep it until commit of the current transaction
        Author a1 = em1.find(Author.class, 1L, LockModeType.PESSIMISTIC_READ);
        LOGGER.info("Transaction 1: " + a1);

        Author a2 = em2.find(Author.class, 1L, LockModeType.PESSIMISTIC_READ);
        LOGGER.info("Transaction 2: " + a2);

        LOGGER.info("Commit transaction 1");
        em1.getTransaction().commit();
        em1.close();

        LOGGER.info("Commit transaction 2");
        em2.getTransaction().commit();
        em2.close();
    }
}
