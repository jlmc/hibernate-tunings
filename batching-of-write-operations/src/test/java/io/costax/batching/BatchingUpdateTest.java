package io.costax.batching;

import io.costax.rules.EntityManagerProvider;
import io.costax.rules.Watcher;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Test nº 3
 */
public class BatchingUpdateTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchingUpdateTest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Rule
    public Watcher watcher = Watcher.timer(LOGGER);

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
