package io.costax.hibernatetunning.customtypes;

import io.costa.hibernatetunings.entities.Developer;
import io.costa.hibernatetunings.entities.Tiket;
import io.costax.hibernatetunning.customtypes.functions.InJPAConsumer;
import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Consistency means that an entity has to have the same hashCode and to be equal to itself for every possible entity state transition:
 * - Transient-->Managed
 * - Managed-->Detached
 * - Detached-->Managed
 * - Managed-->Removed
 */
public class EqualsConsistentCheckTest {

    private static EntityManagerFactory emf;
    private static Developer entity;

    @BeforeClass
    public static void initEntityManagerFactory() {
        entity = new Developer.Builder().setNome("sheldon cooper").createDeveloper();
        emf = Persistence.createEntityManagerFactory("it");
    }

    @AfterClass
    public static void closeEntityManagerFactory() {
        emf.close();
    }

    @Test
    public void assertEqualityConsistency() {
        Set<Developer> tuples = new HashSet<>();

        assertFalse(tuples.contains(entity));
        tuples.add(entity);
        assertTrue(tuples.contains(entity));

        doInJPA(entityManager -> {
            entityManager.persist(entity);
            entityManager.flush();
            assertTrue("The entity is not found in the Set after it's persisted.", tuples.contains(entity));
        });


        doInJPA(em -> {
            final Developer merged = em.merge(entity);
            assertTrue("The entity is not found in the Set after it's merged.", tuples.contains(merged));
        });

        assertTrue(tuples.contains(entity));

        final Developer update = doInJPA(em -> {
            final Developer reference = em.getReference(Developer.class, entity.getId());
            reference.setTiket(Tiket.of("abc", 45.1D));
            final Developer merged = em.merge(reference);
            assertTrue("The entity is not found in the Set after it's merged.", tuples.contains(merged));
            return merged;
        });

        assertTrue("The entity is not found in the Set after it's merged.", tuples.contains(update));

        doInJPA(entityManager -> {
            entityManager.refresh(entity);
            assertTrue("The entity is not found in the Set after it's reattached.", tuples.contains(entity));
        });

        doInJPA(entityManager -> {
            entityManager.unwrap(Session.class).update(entity);
            assertTrue("The entity is not found in the Set after it's reattached.", tuples.contains(entity));
        });

        doInJPA(entityManager -> {
            Developer _entity = entityManager.find(Developer.class, entity.getId());
            assertTrue("The entity is not found in the Set after it's loaded in a subsequent Persistence Context.", tuples.contains(_entity));
        });

        doInJPA(entityManager -> {
            Developer _entity = entityManager.getReference(Developer.class, entity.getId());
            assertTrue("The entity is not in the Set found after it's loaded as a Proxy in an other Persistence Context.", tuples.contains(_entity));
        });

        final Long id = entity.getId();

        Developer deletedEntity = doInJPA(entityManager -> {
            final Developer reference = entityManager.getReference(Developer.class, id);
            entityManager.remove(reference);
            entityManager.flush();
            return reference;
        });

        assertTrue("The entity is found in not the Set even after it's deleted.", tuples.contains(deletedEntity));
    }

    void doInJPA(InJPAConsumer consumer) {
        final EntityManager em = emf.createEntityManager();
        final EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            consumer.accept(em);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            em.close();
        }

    }

    <T> T doInJPA(Function<EntityManager, T> function) {
        final EntityManager em = emf.createEntityManager();
        final EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {

            final T apply = function.apply(em);

            transaction.commit();

            return apply;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
