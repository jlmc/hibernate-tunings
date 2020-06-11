package io.costax.hibernatetunning.persistencecontext;

import io.costax.hibernatetunings.entities.client.Client;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;


@JpaTest(persistenceUnit = "it")
@TestMethodOrder(value = MethodOrderer.Alphanumeric.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class FlushActionOrderTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(FlushActionOrderTest.class);

    @JpaContext
    public JpaProvider provider;

    @Test
    public void t00_should_create_some_clients_records() {
        final EntityManager em = provider.em();
        em.getTransaction().begin();

        em.createNativeQuery("delete from client").executeUpdate();
        em.flush();

        final Client alo = new Client(1, "A-1", "Alo");
        em.persist(alo);

        final Client simmons = new Client(2, "S-1", "Simmons");
        em.persist(simmons);

        final Client bb = new Client(6, "BB", "Born and Burn");
        em.persist(bb);


        em.getTransaction().commit();
        em.close();
    }

    /**
     * by default the remove operation happens after the insert, even when in our implementations have the remove operation before
     */
    @Test
    public void t01_test_operation_order() {
        final EntityManager em = provider.em();
        em.getTransaction().begin();

        final Client one = em.find(Client.class, 1);
        em.remove(one);

        final Client jc = new Client(3, "JC", "Jason Cristy");
        em.persist(jc);

        em.getTransaction().commit();
    }

    /**
     * by default the remove operation happens after the insert, even when in our implementations have the remove operation before.
     * To force the remove to be executed first we must implicitly execute the flush method.
     */
    @Test
    public void t02_test_operation_order_constrain_violations() {
        final EntityManager em = provider.em();
        try {

            final EntityTransaction tx = em.getTransaction();
            tx.begin();


            final Client one = em.find(Client.class, 2);
            em.remove(one);

            final Client jc = new Client(4, "S-1", "Salomon Kean");
            em.persist(jc);

            tx.commit();
        } catch (PersistenceException e) {
            final Throwable cause = e.getCause();

            if ((cause.getCause() instanceof org.hibernate.exception.ConstraintViolationException)) {
                LOGGER.info("Expected problem '{}'", e.getMessage());
            } else {
                Assertions.fail("Expected 'ConstraintViolationException' this should not happen!!!");
            }

        } finally {
            em.close();
        }
    }

    @Test
    public void t03_test_operation_order_with_manual_flush() {
        final EntityManager em = provider.em();
        try {
            em.getTransaction().begin();


            final Client one = em.find(Client.class, 2);
            em.remove(one);

            em.flush();

            final Client jc = new Client(4, "S-1", "Salomon Kean");
            em.persist(jc);

            em.getTransaction().commit();

        } catch (PersistenceException e) {
            final Throwable cause = e.getCause();

            if ((cause.getCause() instanceof org.hibernate.exception.ConstraintViolationException)) {
                throw (ConstraintViolationException) cause.getCause();
            }

        } finally {
            em.close();
        }
    }

    @Test
    public void t04_test_update() {
        UUID random = UUID.randomUUID();

        final AtomicInteger versionHolder = new AtomicInteger(-1);

        provider.doInTx(em -> {
            final Client bb = em.unwrap(Session.class).bySimpleNaturalId(Client.class).load("BB");

            versionHolder.set(bb.getVersion());

            bb.setName("Bing and Binding - " + random);
        });

        final Client bb = provider.em().unwrap(Session.class).bySimpleNaturalId(Client.class).load("BB");
        assertEquals("Bing and Binding - " + random, bb.getName());
    }

    @Test
    public void t05_remove_all_clients() {
        provider.doInTx(em -> em.createQuery("delete from Client").executeUpdate());
    }
}
