package io.costax.hibernatetunning.relationships;

import io.costax.hibernatetunings.entities.Developer;
import io.costax.hibernatetunings.entities.Tiket;
import io.costax.hibernatetunning.functions.InJPAConsumer;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @BeforeAll
    public static void initEntityManagerFactory() {
        entity = new Developer.Builder().setNome("sheldon cooper").createDeveloper();
        emf = Persistence.createEntityManagerFactory("it");
    }

    @AfterAll
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

            assertTrue(tuples.contains(entity), "The entity is not found in the Set after it's persisted.");

        });


        doInJPA(em -> {
            final Developer merged = em.merge(entity);
            assertTrue(tuples.contains(merged), "The entity is not found in the Set after it's merged.");
        });

        assertTrue(tuples.contains(entity));

        final Developer update = doInJPA(em -> {
            final Developer reference = em.getReference(Developer.class, entity.getId());
            reference.setTiket(Tiket.of("abc", 45.1D));
            final Developer merged = em.merge(reference);

            assertTrue(tuples.contains(merged), "The entity is not found in the Set after it's merged.");

            return merged;
        });

        assertTrue(tuples.contains(update), "The entity is not found in the Set after it's merged.");

        doInJPA(entityManager -> {
            entityManager.refresh(entity);

            assertTrue(tuples.contains(entity), "The entity is not found in the Set after it's reattached.");
        });

        doInJPA(entityManager -> {
            entityManager.unwrap(Session.class).update(entity);
            assertTrue(tuples.contains(entity), "The entity is not found in the Set after it's reattached.");
        });

        doInJPA(entityManager -> {
            Developer _entity = entityManager.find(Developer.class, entity.getId());
            assertTrue(tuples.contains(_entity), "The entity is not found in the Set after it's loaded in a subsequent Persistence Context.");
        });

        doInJPA(entityManager -> {
            Developer _entity = entityManager.getReference(Developer.class, entity.getId());
            assertTrue(tuples.contains(_entity), "The entity is not in the Set found after it's loaded as a Proxy in an other Persistence Context.");
        });

        final Long id = entity.getId();

        Developer deletedEntity = doInJPA(entityManager -> {
            final Developer reference = entityManager.getReference(Developer.class, id);
            entityManager.remove(reference);
            entityManager.flush();
            return reference;
        });

        assertTrue(tuples.contains(deletedEntity), "The entity is found in not the Set even after it's deleted.");
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
