package io.github.jlmc.spi.handlers;

import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplicationPostUpdateEventListener implements PostUpdateEventListener {

    public static final ReplicationPostUpdateEventListener INSTANCE = new ReplicationPostUpdateEventListener();

    private static final Logger LOGGER = LoggerFactory.getLogger(ReplicationPostUpdateEventListener.class);

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        LOGGER.info("onPostUpdate, {} - {}", event.getId(), event.getEntity().getClass());
        ReplicationActionHandlerListener.INSTANCE.replicate(
                event, event.getEntity(), event.getId(), "UPDATE"
        );
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        return false;
    }
}
