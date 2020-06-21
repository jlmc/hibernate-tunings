package io.costax.bootstrap_jpa_programmatically;

import io.costax.bootstrap_jpa_programmatically.bootstrap.PersistenceUnitInfoImpl;
import io.costax.bootstrap_jpa_programmatically.database.DataSourceProvider;
import io.costax.bootstrap_jpa_programmatically.database.Database;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractJPAProgrammaticBootstrapTest {

    private EntityManagerFactory emf;

    public EntityManagerFactory entityManagerFactory() {
        return emf;
    }

    @BeforeEach
    public void init() {
        PersistenceUnitInfo persistenceUnitInfo = persistenceUnitInfo(getClass().getSimpleName());

        Map<String, Object> configuration = new HashMap<>();

        Integrator integrator = integrator();
        if (integrator != null) {
            configuration.put(
                    "hibernate.integrator_provider",
                    (IntegratorProvider) () -> Collections.singletonList(integrator));
        }

        emf = new HibernatePersistenceProvider()
                .createContainerEntityManagerFactory(
                    persistenceUnitInfo,
                    configuration
                );
    }

    @AfterEach
    public void destroy() {
        emf.close();
    }

    protected PersistenceUnitInfoImpl persistenceUnitInfo(String name) {
        PersistenceUnitInfoImpl persistenceUnitInfo = new PersistenceUnitInfoImpl(
                name, entityClassNames(), properties()
        );

        String[] resources = resources();
        if (resources != null) {
            persistenceUnitInfo.getMappingFileNames().addAll(Arrays.asList(resources));
        }

        return persistenceUnitInfo;
    }

    protected abstract Class<?>[] entities();

    protected String[] resources() {
        return null;
    }

    protected List<String> entityClassNames() {
        return Arrays.stream(entities()).map(Class::getName).collect(Collectors.toList());
    }

    protected Properties properties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", dataSourceProvider().hibernateDialect());
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        DataSource dataSource = newDataSource();
        if (dataSource != null) {
            properties.put("hibernate.connection.datasource", dataSource);
        }
        properties.put("hibernate.generate_statistics", Boolean.TRUE.toString());

        return properties;
    }

    protected DataSource newDataSource() {
   /*     return proxyDataSource()
                ? dataSourceProxyType().dataSource(dataSourceProvider().dataSource())
                : dataSourceProvider().dataSource();*/
        return dataSourceProvider().dataSource();
    }

    /*
    protected DataSourceProxyType dataSourceProxyType() {
        return DataSourceProxyType.DATA_SOURCE_PROXY;
    }

    protected boolean proxyDataSource() {
        return false;
    }
     */

    protected DataSourceProvider dataSourceProvider() {
        return database().dataSourceProvider();
    }

    protected Database database() {
        return Database.H2;
    }

    protected Integrator integrator() {
        return null;
    }
}

