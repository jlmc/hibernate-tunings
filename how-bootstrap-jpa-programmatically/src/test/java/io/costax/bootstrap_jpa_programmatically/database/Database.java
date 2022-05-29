package io.costax.bootstrap_jpa_programmatically.database;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum Database {
    POSTGRESQL(PostgreSQLDataSourceProvider.class),
    HSQLDB(HSQLDBDataSourceProvider.class),
    H2(H2DataSourceProvider.class);

    private Class<? extends DataSourceProvider> dataSourceProviderClass;

    Database(Class<? extends DataSourceProvider> dataSourceProviderClass) {
        this.dataSourceProviderClass = dataSourceProviderClass;
    }

    public DataSourceProvider dataSourceProvider() {
        return newInstance(dataSourceProviderClass.getName());
    }

    public static <T> T newInstance(String className) {
        try {
            Class<T> clazz = (Class<T>) Class.forName(className);

            final Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();

        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
