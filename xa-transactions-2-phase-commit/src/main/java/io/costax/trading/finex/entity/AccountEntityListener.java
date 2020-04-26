package io.costax.trading.finex.entity;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class AccountEntityListener {

    @Inject
    Event<AuditLog> auditLogEvent;

    @PrePersist
    @PreUpdate
    public void prePersist(Account account) {
        account.getDomainEvents().forEach(auditLogEvent::fire);
        account.clearDomainEvents();
    }
}
