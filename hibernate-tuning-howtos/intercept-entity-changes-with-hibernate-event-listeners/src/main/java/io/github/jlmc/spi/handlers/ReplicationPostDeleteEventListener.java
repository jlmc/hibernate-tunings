package io.github.jlmc.spi.handlers;

import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplicationPostDeleteEventListener implements PostDeleteEventListener {

    public static final ReplicationPostDeleteEventListener INSTANCE = new ReplicationPostDeleteEventListener();

    private static final Logger LOGGER = LoggerFactory.getLogger(ReplicationPostDeleteEventListener.class);

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        LOGGER.info("onPostDelete, {} {}", event.getId(), event.getEntity().getClass());
        ReplicationActionHandlerListener.INSTANCE.replicate(
                event,
                event.getEntity(),
                event.getId(),
                "DELETE");
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        return false;
    }
}
