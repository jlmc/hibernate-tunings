package io.costax.trading.finex.boundary;

import io.costax.trading.finex.control.AuditLogRepository;
import io.costax.trading.finex.entity.AuditLog;
import io.costax.trading.finex.entity.SearchFilter;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static io.costax.trading.finex.control.AuditLogSpecifications.byAuditLogFilter;

@Path("/logs")
@Produces(MediaType.APPLICATION_JSON)
public class LogResources {

    @Inject
    AuditLogRepository auditLogRepository;

    @GET
    public List<AuditLog> list(@Valid @BeanParam SearchFilter filter) {
        return auditLogRepository.findAllBy(byAuditLogFilter(filter));
    }

}
