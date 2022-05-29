package io.github.jlmc.spi.handlers;

import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplicationPostInsertEventListener implements PostInsertEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReplicationPostInsertEventListener.class);

    public static final PostInsertEventListener INSTANCE = new ReplicationPostInsertEventListener();

    @Override
    public void onPostInsert(final PostInsertEvent event) {
        LOGGER.info("onPostInsert, {} - {}", event.getId(), event.getEntity().getClass());
        ReplicationActionHandlerListener.INSTANCE.replicate(
                event,
                event.getEntity(),
                event.getId(),
                "INSERT");
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        return false;
    }
}
