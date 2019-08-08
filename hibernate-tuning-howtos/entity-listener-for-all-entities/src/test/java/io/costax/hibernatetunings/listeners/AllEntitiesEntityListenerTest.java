package io.costax.hibernatetunings.listeners;

import io.costax.hibernatetunings.entities.Video;
import io.costax.rules.EntityManagerProvider;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import java.util.Map;

public class AllEntitiesEntityListenerTest {

    EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void shouldTriggerAllEntityLus() {
        final EntityManager em = provider.em();
        provider.beginTransaction();

        final Video video1 = Video.of(1, "how to use All Entities EntityListener", "how to use All Entities EntityListener");
        em.persist(video1);
        em.flush();

        provider.commitTransaction();

        final Video video2 = em.find(Video.class, 2);
        System.out.println(video2);

        final Map<Class, Long> callbacksCounter = AllEntitiesEntityListener.getCallbacksCounter();
        Assert.assertNotNull(callbacksCounter.get(PrePersist.class));
        Assert.assertEquals(1L, callbacksCounter.get(PrePersist.class).longValue());
        Assert.assertEquals(1L, callbacksCounter.get(PostLoad.class).longValue());
    }
}