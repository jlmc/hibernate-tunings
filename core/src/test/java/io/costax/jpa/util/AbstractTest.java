package io.costax.jpa.util;

import io.costax.jpa.util.transaction.JPATransactionFunction;
import io.costax.jpa.util.providers.DataSourceProvider;
import io.costax.jpa.util.providers.Database;
import io.costax.jpa.util.transaction.JPATransactionVoidFunction;
import org.hibernate.Interceptor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractTest {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private EntityManagerFactory emf;

    @BeforeEach
    public void init() {
        emf = newEntityManagerFactory();
        afterInit();
    }

    @AfterEach
    void tearDown() {
        if (emf != null) {
            emf.close();
        }
    }

    protected void afterInit() {
    }

    protected abstract Class<?>[] entities();

    protected List<String> entityClassNames() {
        return Arrays.stream(entities()).map(Class::getName).collect(Collectors.toList());
    }

    protected EntityManagerFactory newEntityManagerFactory() {
        PersistenceUnitInfo persistenceUnitInfo = persistenceUnitInfo(getClass().getSimpleName());

        Map configuration = properties();
        Interceptor interceptor = interceptor();
        if (interceptor != null) {
            configuration.put(AvailableSettings.INTERCEPTOR, interceptor);
        }
        Integrator integrator = integrator();
        if (integrator != null) {
            configuration.put("hibernate.integrator_provider", (IntegratorProvider) () -> Collections.singletonList(integrator));
        }

        EntityManagerFactoryBuilderImpl entityManagerFactoryBuilder =
                new EntityManagerFactoryBuilderImpl(
                        new PersistenceUnitInfoDescriptor(persistenceUnitInfo),
                        configuration);

        return entityManagerFactoryBuilder.build();
    }

    private Integrator integrator() {
        return null;
    }

    private Interceptor interceptor() {
        return null;
    }

    private PersistenceUnitInfo persistenceUnitInfo(final String persistenceUnitName) {
        final PersistenceUnitInfoImpl persistenceUnitInfo = new PersistenceUnitInfoImpl(persistenceUnitName, entityClassNames(), properties());

        persistenceUnitInfo.getMappingFileNames().addAll(resources());

        return persistenceUnitInfo;
    }

    protected Collection<String> resources() {
        return List.of();
    }

    protected Properties properties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", dataSourceProvider().hibernateDialect());
        //log settings
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        //data source settings
        DataSource dataSource = newDataSource();
        if (dataSource != null) {
            properties.put("hibernate.connection.datasource", dataSource);
        }
        properties.put("hibernate.generate_statistics", Boolean.TRUE.toString());

        //properties.put("net.sf.ehcache.configurationResourceName", Thread.currentThread().getContextClassLoader().getResource("ehcache.xml").toString());
        //properties.put("hibernate.ejb.metamodel.population", "disabled");

        additionalProperties(properties);

        return properties;
    }

    private DataSource newDataSource() {
        return dataSourceProvider().dataSource();
    }

    private DataSourceProvider dataSourceProvider() {
        return database().dataSourceProvider();
    }

    protected abstract Database database();


    protected void additionalProperties(Properties properties) {
    }


    protected <T> T doInJPA(JPATransactionFunction<T> function) {
        T result = null;
        EntityManager entityManager = null;
        EntityTransaction txn = null;
        try {
            entityManager = entityManagerFactory().createEntityManager();

            function.beforeTransactionCompletion();

            txn = entityManager.getTransaction();

            txn.begin();

            result = function.apply(entityManager);

            if (!txn.getRollbackOnly()) {
                txn.commit();
            } else {
                try {
                    txn.rollback();
                } catch (Exception e) {
                    LOGGER.error("Rollback failure", e);
                }
            }
        } catch (Throwable t) {
            if (txn != null && txn.isActive()) {
                try {
                    txn.rollback();
                } catch (Exception e) {
                    LOGGER.error("Rollback failure", e);
                }
            }
            throw t;
        } finally {
            function.afterTransactionCompletion();
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return result;
    }

    protected void doInJPA(JPATransactionVoidFunction function) {
        EntityManager entityManager = null;
        EntityTransaction txn = null;
        try {
            entityManager = entityManagerFactory().createEntityManager();
            function.beforeTransactionCompletion();
            txn = entityManager.getTransaction();
            txn.begin();
            function.accept(entityManager);
            if ( !txn.getRollbackOnly() ) {
                txn.commit();
            }
            else {
                try {
                    txn.rollback();
                }
                catch (Exception e) {
                    LOGGER.error( "Rollback failure", e );
                }
            }
        } catch (Throwable t) {
            if ( txn != null && txn.isActive() ) {
                try {
                    txn.rollback();
                }
                catch (Exception e) {
                    LOGGER.error( "Rollback failure", e );
                }
            }
            throw t;
        } finally {
            function.afterTransactionCompletion();
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    private EntityManagerFactory entityManagerFactory() {
        return emf;
    }
}
