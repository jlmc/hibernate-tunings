package io.github.jlmc.spi.types;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateTypesContributor implements org.hibernate.boot.model.TypeContributor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateTypesContributor.class);

    @Override
    public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {

        LOGGER.info("Registering UseType");

        JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
        //Dialect dialect = jdbcServices.getDialect();

        //typeContributions.contributeType(InetType.INSTANCE);
        //typeContributions.contributeType(InetType.INSTANCE, "inet");

        /*
        MetadataSources sources = new MetadataSources( standardRegistry );
        MetadataBuilder metadataBuilder = sources.getMetadataBuilder();
        metadataBuilder.applyBasicType( BitSetType.INSTANCE );
        */
    }
}
