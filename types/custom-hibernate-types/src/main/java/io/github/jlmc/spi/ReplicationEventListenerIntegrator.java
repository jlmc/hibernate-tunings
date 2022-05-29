package io.github.jlmc.spi;

import io.github.jlmc.spi.handlers.ReplicationPostDeleteEventListener;
import io.github.jlmc.spi.handlers.ReplicationPostInsertEventListener;
import io.github.jlmc.spi.handlers.ReplicationPostUpdateEventListener;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplicationEventListenerIntegrator implements org.hibernate.integrator.spi.Integrator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReplicationEventListenerIntegrator.class);

    @Override
    public void integrate(Metadata metadata,
                          SessionFactoryImplementor sessionFactory,
                          SessionFactoryServiceRegistry serviceRegistry) {
        LOGGER.info("ReplicationEventListenerIntegrator -- deprecated integrate");
    }

    @Override
    public void integrate(Metadata metadata,
                          BootstrapContext bootstrapContext,
                          SessionFactoryImplementor sessionFactory) {
        LOGGER.info("ReplicationEventListenerIntegrator -- integrate");

        SessionFactoryServiceRegistry serviceRegistry = (SessionFactoryServiceRegistry) sessionFactory.getServiceRegistry();
        final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);


        eventListenerRegistry.appendListeners(
                EventType.POST_INSERT,
                ReplicationPostInsertEventListener.INSTANCE
        );


        eventListenerRegistry.appendListeners(
                EventType.POST_UPDATE,
                ReplicationPostUpdateEventListener.INSTANCE
        );

        eventListenerRegistry.appendListeners(
                EventType.POST_DELETE,
                ReplicationPostDeleteEventListener.INSTANCE
        );
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory,
                             SessionFactoryServiceRegistry serviceRegistry) {
        LOGGER.info("ReplicationEventListenerIntegrator -- disintegrate");
    }
}
