package io.costax.hibernatetunings.listeners;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * All Entities EntityListener, handler all jakarta.persistence events for all entities in the persistence Factory.
 * PostLoad
 * PostPersist
 * PostRemove
 * PostUpdate
 * PrePersist
 * PreRemove
 * PreUpdate
 */
public class AllEntitiesEntityListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllEntitiesEntityListener.class);

    private static ConcurrentHashMap<Class, AtomicLong> CALLBACKS_COUNTER;

    public AllEntitiesEntityListener() {
        CALLBACKS_COUNTER = new ConcurrentHashMap<>(Map.of(
                PostLoad.class, new AtomicLong(0),
                PostPersist.class, new AtomicLong(0),
                PostRemove.class, new AtomicLong(0),
                PostUpdate.class, new AtomicLong(0),
                PrePersist.class, new AtomicLong(0),
                PreRemove.class, new AtomicLong(0),
                PreUpdate.class, new AtomicLong(0)
        ));

        LOGGER.info("Start up [{}]", AllEntitiesEntityListener.class);
    }

    public static Map<Class, Long> getCallbacksCounter() {
        return CALLBACKS_COUNTER.entrySet().stream()
                .map(e -> new AbstractMap.SimpleEntry<Class, Long>(e.getKey(), e.getValue().longValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    //@PostLoad
    public void onPostLoadHandler(Object entityInstance) {
        log(PostLoad.class, entityInstance);
    }

    //@PrePersist
    public void onPrePersistHandler(Object entityInstance) {
        log(PrePersist.class, entityInstance);
    }

    private void log(final Class event, final Object entityInstance) {
        LOGGER.info("{} the entity [{}] with is describe by [{}]", event.getName(), entityInstance.getClass().getName(), entityInstance);

        CALLBACKS_COUNTER.get(event).incrementAndGet();
    }
}
