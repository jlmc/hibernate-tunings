package io.costax.hpjp.util.spring.config.flyway;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource({"/META-INF/jdbc-postgresql.properties"})
public class AbstractPostgreSQLFlywayConfiguration extends AbstractFlywayConfiguration {

    @Value("${jdbc.dataSourceClassName}")
    private String dataSourceClassName;

    @Value("${jdbc.username}")
    private String jdbcUser;

    @Value("${jdbc.password}")
    private String jdbcPassword;

    @Value("${jdbc.database}")
    private String jdbcDatabase;

    @Value("${jdbc.host}")
    private String jdbcHost;

    @Value("${jdbc.port}")
    private String jdbcPort;


    @Override
    public DataSource actualDataSource() {
        Properties driverProperties = new Properties();
        driverProperties.setProperty("user", jdbcUser);
        driverProperties.setProperty("password", jdbcPassword);
        driverProperties.setProperty("databaseName", jdbcDatabase);
        driverProperties.setProperty("serverName", jdbcHost);
        driverProperties.setProperty("portNumber", jdbcPort);

        Properties properties = new Properties();
        properties.put("dataSourceClassName", dataSourceClassName);
        properties.put("dataSourceProperties", driverProperties);
        //properties.setProperty("minimumPoolSize", String.valueOf(1));
        properties.setProperty("maximumPoolSize", String.valueOf(3));
        properties.setProperty("connectionTimeout", String.valueOf(5000));
        return new HikariDataSource(new HikariConfig(properties));
    }

    @Override
    protected String databaseType() {
        return "postgresql";
    }
}
