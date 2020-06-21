package io.costax.bootstrap_jpa_programmatically.bootstrap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.*;

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