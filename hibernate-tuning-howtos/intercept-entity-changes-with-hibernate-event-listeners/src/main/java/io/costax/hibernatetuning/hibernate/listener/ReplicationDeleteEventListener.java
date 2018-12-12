package io.costax.hibernatetuning.hibernate.listener;

import io.costax.hibernatetuning.entities.Client;
import org.hibernate.FlushMode;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;

public class ReplicationDeleteEventListener implements PreDeleteEventListener {

    public static final ReplicationDeleteEventListener INSTANCE = new ReplicationDeleteEventListener();

    @Override
    public boolean onPreDelete(final PreDeleteEvent event) {


        final Object entity = event.getEntity();

        if (entity instanceof Client) {

            Client client = (Client) entity;

            event.getSession().createNativeQuery("insert into client_trace (client_id, name, slug, version, deleted) " +
                    "values (:clientId, :name, :slug, :version, :deleted)")
                    .setParameter("clientId", client.getId())
                    .setParameter("name", client.getName())
                    .setParameter("slug", client.getSlug())
                    //.setParameter("createdOn", client.getCreatedOn())
                    .setParameter("version", client.getVersion())
                    .setParameter("deleted", true)
                    .setFlushMode(FlushMode.MANUAL)
                    .executeUpdate();

        }

        return false;
    }
}
