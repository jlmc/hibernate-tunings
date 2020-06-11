package io.costax.hibernatetunings.listeners;

import io.costax.hibernatetunings.entities.Video;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.annotation.Sql;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JpaTest(persistenceUnit = "it")
@Sql(statements = "delete from Video where true", phase = Sql.Phase.BEFORE_TEST_METHOD)
@Sql(statements = "delete from Video where true", phase = Sql.Phase.AFTER_TEST_METHOD)
public class AllEntitiesEntityListenerTest {

    @JpaContext
    JpaProvider provider;

    @PersistenceContext
    EntityManager em;

    @Test
    public void shouldTriggerAllEntityLus() {

        provider.doInTx(em -> {
            final Video video1 = Video.of(1, "how to use All Entities EntityListener", "how to use All Entities EntityListener");
            em.persist(video1);
            em.flush();
        });


        final Video video2 = em.find(Video.class, 2);
        System.out.println(video2);

        final Map<Class, Long> callbacksCounter = AllEntitiesEntityListener.getCallbacksCounter();
        assertNotNull(callbacksCounter.get(PrePersist.class));
        assertEquals(1L, callbacksCounter.get(PrePersist.class).longValue());
    }
}