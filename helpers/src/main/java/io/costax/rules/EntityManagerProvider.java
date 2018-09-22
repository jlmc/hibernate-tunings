package io.costax.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class EntityManagerProvider implements TestRule {

    private final EntityManager em;
    private final EntityTransaction tx;

    private EntityManagerProvider(String persistenceUnitName) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName);

        this.em = emf.createEntityManager();
        this.tx = this.em.getTransaction();
    }

    public static EntityManagerProvider withPersistenceUnit(String persistenceUnitName) {
        return new EntityManagerProvider(persistenceUnitName);
    }

    public EntityManager em() {
        return em;
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
                em.clear();
                em.close();
            }
        };
    }
}

