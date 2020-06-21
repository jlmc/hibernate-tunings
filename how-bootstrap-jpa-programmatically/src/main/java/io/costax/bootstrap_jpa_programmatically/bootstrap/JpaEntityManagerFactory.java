package io.costax.bootstrap_jpa_programmatically.bootstrap;

import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.HibernatePersistenceProvider;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

public class JpaEntityManagerFactory {

    private final String DB_URL = "jdbc:h2:./how-bootstrap-jpa-programmatically/data/db";
    private final String DB_USER_NAME = "sa";
    private final String DB_PASSWORD = "";
    private final Class[] entityClasses;


    private JpaEntityManagerFactory(Class[] entityClasses) {
        this.entityClasses = entityClasses;
    }

    private EntityManagerFactory createEntityManagerFactory(String persistenceUnitName) {
        final PersistenceProvider persistenceProvider = new HibernatePersistenceProvider();

        final PersistenceUnitInfo persistenceInfo  = getPersistenceUnitInfo(persistenceUnitName);
        final Map properties = getProperties();

        return persistenceProvider
                .createContainerEntityManagerFactory(
                persistenceInfo,
                properties);
    }

    public static EntityManagerFactory newEntityManagerFactory(String persistenceUnitName,
                                                                  Class[] entityClasses) {

        return new JpaEntityManagerFactory(entityClasses)
                .createEntityManagerFactory(persistenceUnitName);
    }

    /*
    @Deprecated
    public EntityManagerFactory getEntityManagerFactory() {

        PersistenceUnitInfo persistenceUnitInfo = getPersistenceUnitInfo(
                getClass().getSimpleName());

        Map<String, Object> configuration = new HashMap<>();


        //final DataSource h2DataSource = getH2DataSource();

        //bindDataSourceToContext("jdbc/dsIt", h2DataSource);

        final EntityManagerFactory build = new EntityManagerFactoryBuilderImpl(

                new PersistenceUnitInfoDescriptor(persistenceUnitInfo),
                configuration)
                //.withDataSource(h2DataSource)
                .build();


        return build;
    }
     */

    protected PersistenceUnitInfo getPersistenceUnitInfo(String persistenceUnitName) {
        return new PersistenceUnitInfoImpl(persistenceUnitName, getEntityClassNames(), getProperties());
    }

    private Properties getProperties() {
        final Properties properties = new Properties();

        properties.put(
                "hibernate.dialect",
                H2Dialect.class.getName()
                //dataSourceProvider().hibernateDialect()
        );

        properties.put(
                "hibernate.hbm2ddl.auto",
                "create-drop"
        );

        DataSource dataSource = getH2DataSource();

        if (dataSource != null) {
            properties.put(
                    "hibernate.connection.datasource",
                    dataSource
            );
        }

        properties.put(
                "hibernate.generate_statistics",
                Boolean.TRUE.toString()
        );

        return properties;
    }

    private List<String> getEntityClassNames() {
        return Arrays
                .stream(getEntities())
                .map(Class::getName)
                .collect(Collectors.toList());
    }

    protected Class[] getEntities() {
        return entityClasses;
    }

    protected DataSource getH2DataSource() {
        final JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(DB_URL);
        ds.setUser(DB_USER_NAME);
        ds.setPassword(DB_PASSWORD);

        return ds;
    }

    @SuppressWarnings("unused")
    protected void bindDataSourceToContext(String name, DataSource dataSource) {

        try {
            Context ctx = new InitialContext();
            //ctx.bind("jdbc/dsName", dataSource);
            ctx.bind(name, dataSource);
        } catch (NamingException e) {
            throw new IllegalStateException(e);
        }
    }


}
