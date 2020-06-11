package io.costax.concurrency.optimistic;

import io.costax.concurrency.domain.books.Publisher;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.Test;

/**
 * Test nº 3
 */
@JpaTest(persistenceUnit = "it")
public class OptimisticLockWithDateTest {

    @JpaContext
    public JpaProvider provider;

    @Test
    public void optimisticLockWithDateTest() {
        provider.doInTx(em -> {
            final Publisher publisher = em.find(Publisher.class, 1);
            publisher.setName(publisher.getName() + " -- updated");
        });
    }
}
