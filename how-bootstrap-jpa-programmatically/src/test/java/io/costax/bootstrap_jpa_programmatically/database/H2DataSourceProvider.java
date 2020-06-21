package io.costax.bootstrap_jpa_programmatically.database;

import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.dialect.H2Dialect;

import javax.sql.DataSource;
import java.util.Properties;

public class H2DataSourceProvider implements DataSourceProvider {
    @Override
    public String hibernateDialect() {
        return H2Dialect.class.getName();
    }

    @Override
    public DataSource dataSource() {
        final JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        //ds.setUser("sa");
        //ds.setPassword("");
        return ds;
    }

    @Override
    public Class<? extends DataSource> dataSourceClassName() {
        return JdbcDataSource.class;
    }

    @Override
    public Properties dataSourceProperties() {
        Properties properties = new Properties();
        properties.setProperty("databaseName", "example-db");
        properties.setProperty("serverName", "localhost");
        properties.setProperty("user", username());
        properties.setProperty("password", password());
        return properties;
    }

    @Override
    public String url() {
        return null;
    }

    @Override
    public String username() {
        return "sa";
    }

    @Override
    public String password() {
        return "";
    }

    @Override
    public Database database() {
        return Database.H2;
    }
}
