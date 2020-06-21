package io.costax.jpa.util.providers;

import io.costax.reflection.Reflections;

public enum Database {

    HSQLDB(HSQLDBDataSourceProvider.class),
    POSTGRESQL(PostgreSQLDataSourceProvider.class);

    private Class<? extends DataSourceProvider> dataSourceProviderClass;

    Database(Class<? extends DataSourceProvider> dataSourceProviderClass) {
        this.dataSourceProviderClass = dataSourceProviderClass;
    }

    public DataSourceProvider dataSourceProvider() {
        return Reflections.newInstance(dataSourceProviderClass.getName());
    }
}
