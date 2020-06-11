package io.costax.batching;

import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;

/**
 * Test nº 2
 */
@JpaTest(persistenceUnit = "it")
public class BatchingInsertEntityWithDependentsTest {

    @JpaContext
    public JpaProvider provider;

    @Test
    public void testInsertActorsSerie() {
        EntityManager em = provider.em();
        em.getTransaction().begin();

        for (int i = 0; i < 10; i++) {
            Author actor = Author.of("First-Name-" + i, "LastName" + i);

            em.persist(actor);

            io.costax.batching.Book b = Book.of("Title-" + i, "Description-" + 1);
            b.addActor(actor);

            em.persist(b);
        }

        em.getTransaction().commit();
        em.close();
    }
}
