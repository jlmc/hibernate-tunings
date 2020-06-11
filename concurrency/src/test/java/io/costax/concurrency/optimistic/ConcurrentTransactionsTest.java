package io.costax.concurrency.optimistic;

import io.costax.concurrency.domain.books.Author;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;

import static org.junit.jupiter.api.Assertions.fail;

@JpaTest(persistenceUnit = "it")
public class ConcurrentTransactionsTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptimisticLockTest.class);

    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    public void testConcurrentUpdate() {

        final EntityManager em1 = emf.createEntityManager();

        em1.getTransaction().begin();

        EntityManager em2 = emf.createEntityManager();
        em2.getTransaction().begin();

        Author a1 = em1.find(Author.class, 1L);
        a1.setFirstName("changed");

        Author a2 = em2.find(Author.class, 1L);
        a2.setLastName("something else");

        em1.getTransaction().commit();
        em1.close();

        try {
            em2.getTransaction().commit();

            fail("RollbackExecption expected");
        } catch (RollbackException e) {
            if (e.getCause() instanceof OptimisticLockException) {
                LOGGER.info("{0}", e.getCause());
            } else {
                fail("OptimisticLockException expected");
            }
        }

        em2.close();
    }
}
