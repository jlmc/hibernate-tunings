package io.costax.bootstrap_jpa_programmatically.bootstrap;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JpaEntityManagerFactoryTest {

    private EntityManagerFactory emf;

    @BeforeEach
    void setUp() {
        this.emf = JpaEntityManagerFactory.newEntityManagerFactory(
                getClass().getSimpleName(),
                new Class[]{
                        Book.class
                });
    }

    @AfterEach
    void tearDown() {
        emf.close();
    }

    @Test
    void test() {
        EntityManager em = null;
        EntityTransaction tx = null;
        try {

            em = emf.createEntityManager();
            tx = em.getTransaction();

            tx.begin();

            em.persist(new Book("Bootstrap hibernate programmatically"));

            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}

@Entity(name = "Book")
class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    public Book(final String title) {
        this.title = title;
    }
}
