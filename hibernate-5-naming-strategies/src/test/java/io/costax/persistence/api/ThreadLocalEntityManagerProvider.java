package io.costax.persistence.api;

import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.sql.Connection;
import java.util.function.Consumer;
import java.util.function.Function;

public class ThreadLocalEntityManagerProvider {

    private static final ThreadLocal<EntityManagerFactory> ENTITY_MANAGER_PROVIDER_THREAD = new ThreadLocal<>();

    private ThreadLocalEntityManagerProvider() {
    }

    public static void withPersistenceUnit(String persistenceUnitName) {
        if (ENTITY_MANAGER_PROVIDER_THREAD.get() != null && ENTITY_MANAGER_PROVIDER_THREAD.get().isOpen())
            throw new IllegalStateException("The Entity Manager Factory is already exists and is Open!");

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);

        ENTITY_MANAGER_PROVIDER_THREAD.set(entityManagerFactory);
    }

    public static void shutdown() {
        EntityManagerFactory entityManagerFactory = ENTITY_MANAGER_PROVIDER_THREAD.get();
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }

    public static EntityManager em() {
        return ENTITY_MANAGER_PROVIDER_THREAD.get()
                .createEntityManager();
    }

    public static void doIt(Consumer<EntityManager> consumer) {
        EntityManager em = em();
        try {

            consumer.accept(em);

            //em.flush();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public static <T> T doIt(Function<EntityManager, T> function) {
        final EntityManager em = em();
        try {

            return function.apply(em);

        } finally {
            em.close();
        }
    }

    public static void doItTx(Consumer<EntityManager> consumer) {
        EntityManager em = em();
        EntityTransaction tx = null;

        try {

            tx = em.getTransaction();
            tx.begin();

            consumer.accept(em);

            em.flush();
            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public static <T> T doJDBCReturningWork(Function<Connection, T> function) {
        final EntityManager em = em();
        try {

            return em.unwrap(Session.class).doReturningWork(function::apply);

        } finally {
            em.close();
        }
    }

}
