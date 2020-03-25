package io.costax.concurrency.pessimistic.books;

import io.costax.concurrency.domain.books.Author;
import io.costax.rules.EntityManagerProvider;
import io.costax.rules.Watcher;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

public class ReadLockTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadLockTest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Rule
    public Watcher watcher = Watcher.timer(LOGGER);

    @Test
    public void testReadLock() {

        EntityManager em1 = provider.createdEntityManagerUnRuled();
        em1.getTransaction().begin();

        EntityManager em2 = provider.createdEntityManagerUnRuled();
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
