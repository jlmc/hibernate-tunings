package io.costax.jpa.util.providers;

import org.hibernate.dialect.PostgreSQL10Dialect;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.util.Properties;

public class PostgreSQLDataSourceProvider implements DataSourceProvider {

    @Override
    public String hibernateDialect() {
        return PostgreSQL10Dialect.class.getName();
    }

    @Override
    public DataSource dataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setDatabaseName(databaseName());
        dataSource.setServerName(serverName());
        dataSource.setPortNumber(5432);
        dataSource.setUser("postgres");
        dataSource.setPassword("postgres");
        return dataSource;
    }

    @Override
    public Class<? extends DataSource> dataSourceClassName() {
        return PGSimpleDataSource.class;
    }

    @Override
    public Properties dataSourceProperties() {
        Properties properties = new Properties();
        properties.setProperty("databaseName", databaseName());
        properties.setProperty("serverName", serverName());
        properties.setProperty("user", username());
        properties.setProperty("password", password());
        return properties;
    }

    private String serverName() {
        return "localhost";
    }

    private String databaseName() {
        return "postgresdemos";
    }

    @Override
    public String url() {
        return "jdbc:postgresql://localhost:5432/postgresdemos";
    }

    @Override
    public String username() {
        return "postgres";
    }

    @Override
    public String password() {
        return "postgres";
    }

    @Override
    public Database database() {
        return Database.POSTGRESQL;
    }

}
