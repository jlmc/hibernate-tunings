package io.costax.trading.finex.boundary;

import io.costax.trading.finex.control.AccountRepository;
import io.costax.trading.finex.control.AccountSpecifications;
import io.costax.trading.finex.control.AuditLogRepository;
import io.costax.trading.finex.control.AuditLogSpecifications;
import io.costax.trading.finex.entity.Account;
import io.costax.trading.finex.entity.AuditLog;
import io.costax.trading.finex.entity.SearchFilter;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Stateless
public class ExportationDataService {

    @Inject
    AccountRepository accountRepository;

    @Inject
    AuditLogRepository auditLogRepository;


    public Map<String, Object> exportAllData(SearchFilter filter) {
        List<Account> accounts = accountRepository.listAll(AccountSpecifications.by(filter));
        List<AuditLog> auditLogs = auditLogRepository.findAllBy(AuditLogSpecifications.byAuditLogFilter(filter));

        return Map.of("accounts", accounts, "auditLogs", auditLogs);
    }
}
