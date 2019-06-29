package io.costax.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.function.Consumer;

public class EntityManagerProvider implements TestRule {
    private EntityManagerFactory emf;
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
        final EntityManager em = emf.createEntityManager();
        try {

            consumer.accept(em);

        } catch (Exception e) {
            throw e;
        } finally {
            em.close();
        }
    }

    public void doInTx(Consumer<EntityManager> consumer) {
        final EntityManager em = emf.createEntityManager();
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
}

