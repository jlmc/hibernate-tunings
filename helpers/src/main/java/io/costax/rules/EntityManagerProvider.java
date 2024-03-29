package io.costax.rules;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.hibernate.Session;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.sql.Connection;
import java.util.function.Consumer;
import java.util.function.Function;

public class EntityManagerProvider implements TestRule {
    private final EntityManagerFactory emf;
    private final EntityManager em;
    private final EntityTransaction tx;

    private EntityManagerProvider(String persistenceUnitName) {
        this.emf = Persistence.createEntityManagerFactory(persistenceUnitName);


        /*
        cast the EntityManager to EntityManagerImpl (the Hibernate implementation)
        call getFactory()
        cast the EntityManagerFactory to HibernateEntityManagerFactory
        call getSessionFactory() and cast it to SessionFactoryImpl
        call getConnectionProvider() and cast it to the correct implementation. You can see the implementations here. I'll assume that it's a DatasourceConnectionProvider
        call getDataSource() and you're done.
        */

        this.em = emf.createEntityManager();
        this.tx = this.em.getTransaction();
    }

//    void ds() {
//        HibernateEntityManagerFactory hibernateEntityManagerFactory = (HibernateEntityManagerFactory) emf;
//        SessionFactoryImpl sessionFactory = (SessionFactoryImpl) hibernateEntityManagerFactory.getSessionFactory();
//        sessionFactory.get();
//    }

    public static EntityManagerProvider withPersistenceUnit(String persistenceUnitName) {
        return new EntityManagerProvider(persistenceUnitName);
    }

    public EntityManager em() {
        return em;
    }

    public EntityManager createdEntityManagerUnRuled() {
        return emf.createEntityManager();
    }

    public EntityTransaction tx() {
        return tx;
    }

    public void commitTransaction() {
        this.tx.commit();
    }

    public void beginTransaction() {
        this.tx.begin();
    }

    public void rollbackTransaction() {
        this.tx.rollback();
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                base.evaluate();

                if (em.isOpen()) {
                    em.clear();
                    em.close();
                }
            }
        };
    }

    public void doIt(Consumer<EntityManager> consumer) {
        EntityManager em = emf.createEntityManager();
        try {

            consumer.accept(em);

        } catch (Exception e) {
            throw e;
        } finally {
            em.close();
        }
    }

    public <T> T doIt(Function<EntityManager, T> function) {
        EntityManager em = emf.createEntityManager();
        try {

            return function.apply(em);

        } finally {
            em.close();
        }
    }

    public void doInTx(Consumer<EntityManager> consumer) {
        EntityManager em = emf.createEntityManager();
        try {
            EntityTransaction tx;
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

    public <T> T doJDBCReturningWork(Function<Connection, T> function) {
        EntityManager em = emf.createEntityManager();
        try {

            return em.unwrap(Session.class).doReturningWork(function::apply);

        } finally {
            em.close();
        }
    }
}
