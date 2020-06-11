package io.costax.concurrency.optimistic;

import io.costax.concurrency.domain.books.Author;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.Test;

/**
 * 1
 */
@JpaTest(persistenceUnit = "it")
public class OptimisticLockTest {

    @JpaContext
    public JpaProvider provider;

    @Test
    public void testUpdate() {
        provider.doInTx(em -> {
            Author a = em.find(Author.class, 1L);
            a.setFirstName("Saramago");
        });
    }
}
