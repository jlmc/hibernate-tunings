package io.costax.concurrency.optimistic;

import batching.Author;
import io.costax.rules.EntityManagerProvider;
import io.costax.rules.Watcher;
import org.hibernate.jpa.QueryHints;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

public class ForceVersionIncrementTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForceVersionIncrementTest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Rule
    public Watcher watcher = Watcher.timer(LOGGER);

    @Test
    public void updateUsingQuery() {
        final EntityManager em = provider.em();
        provider.beginTransaction();

        Author a = em.createQuery(
                "select distinct a from Author a left join fetch a.books where a.id = :id", Author.class)
                .setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
                .setParameter("id", 1L)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getSingleResult();


        a.getBooks().forEach(b -> b.setTitle(b.getTitle() + " - 2nd Edition"));

        provider.commitTransaction();
    }

    @Test
    public void updateUsingFind() {
        final EntityManager em = provider.em();
        provider.beginTransaction();

        Author a = em.find(Author.class, 2L, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

        a.getBooks().forEach(b -> b.setTitle(b.getTitle() + " - 2nd Edition"));

        provider.commitTransaction();
    }
}
