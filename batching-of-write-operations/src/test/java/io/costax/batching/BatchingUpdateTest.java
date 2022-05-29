package io.costax.batching;

import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import java.util.List;

/**
 * Test nº 3
 */
@JpaTest(persistenceUnit = "it")
public class BatchingUpdateTest {

    @JpaContext
    public JpaProvider provider;

    @Test
    public void testUpdateActorsBooks() {
        EntityManager em = provider.em();
        em.getTransaction().begin();

        List<Author> authors = em
                .createQuery("SELECT a FROM Author a JOIN FETCH a.books b", Author.class).getResultList();

        for (Author a : authors) {
            a.setFirstName(a.getFirstName() + " - updated");

            a.getBooks().forEach(b -> b.setTitle(b.getTitle() + " - updated"));
        }

        em.getTransaction().commit();
        em.close();
    }
}
