package io.costax.hibernatetunning.persistencecontext;

import io.costa.hibernatetunings.entities.client.Client;
import io.costax.rules.EntityManagerProvider;
import org.hamcrest.Matchers;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

// TODO: refactor this test
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FlushActionOrderTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void t00_should_create_some_clients_records() {
        provider.beginTransaction();

        provider.em().createNativeQuery("delete from client c").executeUpdate();
        provider.em().flush();


        final EntityManager em = provider.em();

        final Client alo = new Client(1, "A-1", "Alo");
        em.persist(alo);

        final Client simmons = new Client(2, "S-1", "Simmons");
        em.persist(simmons);

        final Client bb = new Client(6, "BB", "Born and Burn");
        em.persist(bb);

        provider.commitTransaction();
    }

    /**
     * by default the remove operation happens after the insert, even when in our implementations have the remove operation before
     */
    @Test
    public void t01_test_operation_order() {
        provider.beginTransaction();
        final EntityManager em = provider.em();

        final Client one = em.find(Client.class, 1);
        em.remove(one);

        final Client jc = new Client(3, "JC", "Jason Cristy");
        em.persist(jc);

        provider.commitTransaction();
    }

    /**
     * by default the remove operation happens after the insert, even when in our implementations have the remove operation before.
     * To force the remove to be executed first we must implicitly execute the flush method.
     */
    @Test(expected = org.hibernate.exception.ConstraintViolationException.class)
    public void t02_test_operation_order_constrain_violations() {
        try {
            provider.beginTransaction();
            final EntityManager em = provider.em();

            final Client one = em.find(Client.class, 2);
            em.remove(one);

            final Client jc = new Client(4, "S-1", "Salomon Kean");
            em.persist(jc);

            provider.commitTransaction();
        } catch (PersistenceException e) {
            final Throwable cause = e.getCause();

            if ((cause.getCause() instanceof org.hibernate.exception.ConstraintViolationException)) {
                throw (ConstraintViolationException) cause.getCause();
            } else {
                throw e;
            }
        }
    }

    @Test
    public void t03_test_operation_order_with_manual_flush() {
        try {
            provider.beginTransaction();
            final EntityManager em = provider.em();

            final Client one = em.find(Client.class, 2);
            em.remove(one);

            em.flush();

            final Client jc = new Client(4, "S-1", "Salomon Kean");
            em.persist(jc);

            provider.commitTransaction();
        } catch (PersistenceException e) {
            final Throwable cause = e.getCause();

            if ((cause.getCause() instanceof org.hibernate.exception.ConstraintViolationException)) {
                throw (ConstraintViolationException) cause.getCause();
            } else {
                throw e;
            }
        }
    }

    @Test
    public void t04_test_update() {
        UUID random = UUID.randomUUID();

        final AtomicInteger versionHolder = new AtomicInteger(-1);


        provider.doInTx(em -> {
            final Client bb = provider.em().unwrap(Session.class).bySimpleNaturalId(Client.class).load("BB");

            versionHolder.set(bb.getVersion());

            bb.setName("Bing and Binding - " + random);
        });

        final Client bb = provider.em().unwrap(Session.class).bySimpleNaturalId(Client.class).load("BB");
        Assert.assertThat(bb.getName(), Matchers.is("Bing and Binding - " + random));
        //Assert.assertThat(bb.getVersion(), greaterThan(versionHolder.get()));
    }


    @Test
    public void t05_remove_all_clients() {
        provider.beginTransaction();

        provider.em().createQuery("delete from Client").executeUpdate();

        provider.commitTransaction();
    }
}
