package io.costax.jpa.util.providers;

import javax.sql.DataSource;
import java.util.Properties;

public interface DataSourceProvider {

    String hibernateDialect();

    DataSource dataSource();

    Class<? extends DataSource> dataSourceClassName();

    Properties dataSourceProperties();

    String url();

    String username();

    String password();

    Database database();
}
