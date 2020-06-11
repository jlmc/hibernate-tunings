package io.costax.batching;

import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Test nº 4
 */
@JpaTest(persistenceUnit = "it")
public class BatchingDeleteEntityWithoutBatchTest {

    @JpaContext
    public JpaProvider provider;

    @Test
    public void testDeleteActorsWithoutBatch() {

        EntityManager em = provider.em();
        em.getTransaction().begin();

        List<Author> authors = em.createQuery("SELECT a FROM Author a", Author.class).getResultList();

        for (Author a : authors) {
            em.remove(a);
        }

        em.getTransaction().commit();
        em.close();
    }

}
