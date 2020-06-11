package io.costax.concurrency.optimistic;

import io.costax.concurrency.domain.books.Author;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import org.hibernate.jpa.QueryHints;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;


@JpaTest(persistenceUnit = "it")
public class ForceVersionIncrementTest {

    @PersistenceContext
    EntityManager em;

    @Test
    public void updateUsingQuery() {
        em.getTransaction().begin();

        Author a = em.createQuery(
                "select distinct a from Author a left join fetch a.books where a.id = :id", Author.class)
                .setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
                .setParameter("id", 1L)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getSingleResult();


        a.getBooks().forEach(b -> b.setTitle(b.getTitle() + " - 2nd Edition"));

        em.getTransaction().commit();
    }

    @Test
    public void updateUsingFind() {
        em.getTransaction().begin();

        Author a = em.find(Author.class, 2L, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

        a.getBooks().forEach(b -> b.setTitle(b.getTitle() + " - 2nd Edition"));

        em.getTransaction().commit();
    }
}
