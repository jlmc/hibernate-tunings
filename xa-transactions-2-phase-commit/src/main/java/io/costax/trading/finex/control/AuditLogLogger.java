package io.costax.trading.finex.control;


import io.costax.trading.finex.entity.AuditLog;

import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;

public class AuditLogLogger {

    @Inject
    AuditLogRepository auditLogRepository;

    public void trace(@Observes(during = TransactionPhase.BEFORE_COMPLETION)
                              AuditLog auditLog) {
        auditLogRepository.save(auditLog);
    }
}
