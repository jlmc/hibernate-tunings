package io.costax.trading.finex.boundary;

import io.costax.trading.finex.entity.SearchFilter;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/exportation")
@Produces(MediaType.APPLICATION_JSON)
public class ExportDataResource {

    @Inject
    ExportationDataService exportationDataService;

    @GET
    public Map<String, Object> get(@BeanParam SearchFilter searchFilter) {
        return exportationDataService.exportAllData(searchFilter);
    }
}
