package io.costax.core.persistence;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAggregateRoot<T> {

    @Transient
    private final transient List<T> domainEvents = new ArrayList<>();

    public AbstractAggregateRoot() {
    }

    protected void addDomainEvent(T event) {
        domainEvents.add(event);
    }


    public List<T> getDomainEvents() {
        return List.copyOf(domainEvents);
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
}
