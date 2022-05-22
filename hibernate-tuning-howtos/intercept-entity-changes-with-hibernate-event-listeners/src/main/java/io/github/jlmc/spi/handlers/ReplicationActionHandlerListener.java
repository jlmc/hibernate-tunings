package io.github.jlmc.spi.handlers;

import io.github.jlmc.spi.Replicable;
import io.github.jlmc.spi.json.JsonProducer;
import org.hibernate.FlushMode;
import org.hibernate.event.spi.AbstractEvent;

public class ReplicationActionHandlerListener {

    static final ReplicationActionHandlerListener INSTANCE = new ReplicationActionHandlerListener();

    public void replicate(AbstractEvent event,
                          Object object,
                          Object id,
                          String operation
                          ) {
        if (!isReplicable(object)) return;

        String json = JsonProducer.toJson(object);

        event.getSession()
             .createNativeQuery(
                     """
                             insert into entity_trace (entity_id, type, operation, snapshot) 
                             values (:entityId, :type, :operation, :snapshot)
                             """
             )
             .setParameter("entityId", id)
             .setParameter("type", object.getClass().getSimpleName())
             .setParameter("operation", operation)
             .setParameter("snapshot", json)
             .setHibernateFlushMode(FlushMode.MANUAL)
             .executeUpdate();
    }

    private boolean isReplicable(Object object) {
        if (object == null) return false;

        Class<?> aClass = object.getClass();

        return aClass.isAnnotationPresent(Replicable.class);
    }
}
