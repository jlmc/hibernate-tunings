package io.costax.batching;

import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import java.util.List;

/**
 * Test nº 5
 */
@JpaTest(persistenceUnit = "it")
public class BatchingDeleteEntityWithBatchTest {

    @JpaContext
    public JpaProvider provider;

    @Test
    public void testDeleteActorsWithBatch() {

        EntityManager em = provider.em();
        em.getTransaction().begin();

        em.createQuery("DELETE Review r")
                .executeUpdate();

        em.createQuery("DELETE Book b")
                .executeUpdate();

        List<Author> authors = em.createQuery("SELECT a FROM Author a LEFT JOIN FETCH a.books", Author.class).getResultList();

        for (Author a : authors) {
            em.remove(a);
        }

        em.getTransaction().commit();
        em.close();
    }
}
