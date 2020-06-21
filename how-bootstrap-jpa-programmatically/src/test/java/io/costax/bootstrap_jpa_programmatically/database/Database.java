package io.costax.bootstrap_jpa_programmatically.database;

import io.costax.reflection.Reflections;

public enum Database {
    POSTGRESQL(PostgreSQLDataSourceProvider.class),
    HSQLDB(HSQLDBDataSourceProvider.class),
    H2(H2DataSourceProvider.class);

    private Class<? extends DataSourceProvider> dataSourceProviderClass;

    Database(Class<? extends DataSourceProvider> dataSourceProviderClass) {
        this.dataSourceProviderClass = dataSourceProviderClass;
    }

    public DataSourceProvider dataSourceProvider() {
        return Reflections.newInstance(dataSourceProviderClass.getName());
    }

}
