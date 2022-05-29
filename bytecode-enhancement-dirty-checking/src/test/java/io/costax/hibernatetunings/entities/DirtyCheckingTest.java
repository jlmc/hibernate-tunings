package io.costax.hibernatetunings.entities;

import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.annotation.Sql;
import io.github.jlmc.jpa.test.annotation.SqlGroup;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import static io.github.jlmc.jpa.test.annotation.Sql.Phase.AFTER_TEST_METHOD;
import static io.github.jlmc.jpa.test.annotation.Sql.Phase.BEFORE_TEST_METHOD;

@JpaTest(persistenceUnit = "it")
@SqlGroup({
        @Sql(statements = "delete from public.article", phase = BEFORE_TEST_METHOD),
        @Sql(statements = "delete from public.article", phase = AFTER_TEST_METHOD)
})
public class DirtyCheckingTest {

    @PersistenceContext
    EntityManager em;

    @Test
    @Order(0)
    @DisplayName("Dirty Checking")
    public void dirtyChecking() {
        // First Transaction
        {
            em.getTransaction().begin();

            final Article article = Article.of(1, "JPA-with-Hibernate");
            em.persist(article);

            em.getTransaction().commit();
        }

        // Second Transaction
        {
            em.getTransaction().begin();

            final Article article = em.find(Article.class, 1);
            article.setName("Hibernate Enhance Dirty Checking Mechanism");

            em.flush();

            em.getTransaction().commit();
        }

    }

    @Test
    @Order(1)
    @DisplayName("Remove all data using native query with FlushMode.ALWAYS")
    public void removeAllData() {
        em.getTransaction().begin();

        final Session session = em.unwrap(Session.class);

        session.createNativeQuery(
                "delete from article where id > 0")
                .setHibernateFlushMode(FlushMode.ALWAYS)
                .addSynchronizedEntityClass(Article.class)
                .executeUpdate();

        em.getTransaction().commit();
    }
}
