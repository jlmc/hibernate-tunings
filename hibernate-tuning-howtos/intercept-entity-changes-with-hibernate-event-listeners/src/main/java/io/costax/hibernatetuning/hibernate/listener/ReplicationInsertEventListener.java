package io.costax.hibernatetuning.hibernate.listener;

import io.costax.hibernatetuning.entities.Client;
import org.hibernate.FlushMode;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;

public class ReplicationInsertEventListener implements PostInsertEventListener {

    public static final PostInsertEventListener INSTANCE = new ReplicationInsertEventListener();

    @Override
    public void onPostInsert(final PostInsertEvent event) {

        final Object entity = event.getEntity();

        if (entity instanceof Client) {
            Client client = (Client) entity;

            event.getSession().createNativeQuery(
                    "insert into client_trace (client_id, name, slug, version, deleted) " +
                            "values (:clientId, :name, :slug, :version, :deleted)")
                    .setParameter("clientId", client.getId())
                    .setParameter("name", client.getName())
                    .setParameter("slug", client.getSlug())
                    .setParameter("version", client.getVersion())
                    .setParameter("deleted", false)
                    .setFlushMode(FlushMode.MANUAL)
                    .executeUpdate();

        }

    }

    @Override
    public boolean requiresPostCommitHanding(final EntityPersister persister) {
        return false;
    }
}
