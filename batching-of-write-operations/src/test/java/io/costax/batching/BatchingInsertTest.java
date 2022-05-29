package io.costax.batching;

import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

/**
 * Test nº 1
 */
@JpaTest(persistenceUnit = "it")
public class BatchingInsertTest {

    @JpaContext
    public JpaProvider provider;

    @Test
    public void testInsertActors() {
        EntityManager em = provider.em();
        em.getTransaction().begin();

        for (int i = 1; i <= 20; i++) {
            Author actor = Author.of("First-Name-" + i, "LastName" + i);

            em.persist(actor);

            if (i % 5 == 0) {
                em.flush();
                em.clear();
            }
        }

        em.getTransaction().commit();
        em.close();
    }
}
