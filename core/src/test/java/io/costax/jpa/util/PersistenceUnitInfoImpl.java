package io.costax.jpa.util;

import org.hibernate.jpa.HibernatePersistenceProvider;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * <h2>Implementing the PersistenceUnitInfo Interface</h2>
 * <p>
 * In a typical “xml-based” JPA configuration, the JPA implementation automatically takes care of implementing the PersistenceUnitInfo interface.
 * <p>
 * Using all the data gathered by parsing the “persistence.xml” file, the persistence provider uses this implementation to create an entity manager factory.
 * From this factory, we can obtain an entity manager.
 * <p>
 * Since we won't rely on the “persistence.xml” file, the first thing that we need to do is to provide our own PersistenceUnitInfo implementation.
 * <p>
 * <p>
 * The JPA specification PersistenceUnitInfo interface encapsulates everything is needed for bootstrapping an EntityManagerFactory. Typically, this interface is implemented by the JPA provider to store the info retrieved after parsing the persistence.xml configuration file.
 * <p>
 * Because we will no longer use a persistence.xml configuration file, we have to implement this interface ourselves. For the purpose of this test, consider the following implementation:
 */
public class PersistenceUnitInfoImpl implements PersistenceUnitInfo {

    public static final String JPA_VERSION = "2.2";

    private final String persistenceUnitName;
    private final List<String> managedClassNames;
    private final List<String> mappingFileNames = new ArrayList<>();
    private final Properties properties;
    private PersistenceUnitTransactionType transactionType = PersistenceUnitTransactionType.RESOURCE_LOCAL;
    private DataSource jtaDataSource;
    private DataSource nonJtaDataSource;
    private List<ClassTransformer> transformers = new ArrayList<>();


    public PersistenceUnitInfoImpl(
            String persistenceUnitName,
            List<String> managedClassNames,
            Properties properties) {
        this.persistenceUnitName = persistenceUnitName;
        this.managedClassNames = managedClassNames;
        this.properties = properties;
    }

    @Override
    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    @Override
    public String getPersistenceProviderClassName() {
        final String persistenceProviderClassName = HibernatePersistenceProvider.class.getName();
        return persistenceProviderClassName;
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return this.transactionType;
    }

    @Override
    public DataSource getJtaDataSource() {
        return this.jtaDataSource;
    }

    public PersistenceUnitInfoImpl setJtaDataSource(DataSource jtaDataSource) {
        this.jtaDataSource = jtaDataSource;
        this.nonJtaDataSource = null;
        transactionType = PersistenceUnitTransactionType.JTA;
        return this;
    }

    @Override
    public DataSource getNonJtaDataSource() {
        return this.nonJtaDataSource;
    }

    public PersistenceUnitInfoImpl setNonJtaDataSource(DataSource nonJtaDataSource) {
        this.nonJtaDataSource = nonJtaDataSource;
        this.jtaDataSource = null;
        transactionType = PersistenceUnitTransactionType.RESOURCE_LOCAL;
        return this;
    }

    @Override
    public List<String> getMappingFileNames() {
        return mappingFileNames;
    }

    @Override
    public List<URL> getJarFileUrls() {
        return Collections.emptyList();
    }

    @Override
    public URL getPersistenceUnitRootUrl() {
        return null;
    }

    @Override
    public List<String> getManagedClassNames() {
        return managedClassNames;
    }

    @Override
    public boolean excludeUnlistedClasses() {
        return false;
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
        return SharedCacheMode.UNSPECIFIED;
    }

    @Override
    public ValidationMode getValidationMode() {
        return ValidationMode.AUTO;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public String getPersistenceXMLSchemaVersion() {
        return JPA_VERSION;
    }

    @Override
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @Override
    public void addTransformer(final ClassTransformer transformer) {
        this.transformers.add(transformer);
    }

    @Override
    public ClassLoader getNewTempClassLoader() {
        return null;
    }
}
