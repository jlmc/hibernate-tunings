package io.costax.trading.finex.boundary;

import io.costax.trading.finex.control.AccountRepository;
import io.costax.trading.finex.entity.Account;

import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResources {

    @Inject
    AccountRepository accountRepository;

    @Inject
    TradingService tradingService;

    @Context
    UriInfo uriInfo;

    @GET
    public List<Account> list() {
        return accountRepository.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") UUID id) {
        return accountRepository.findById(id)
                .map(Response::ok)
                .map(Response.ResponseBuilder::build)
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .header("X-reason", String.format("Resource [%s] with identifier [%d] not found", Account.class.getSimpleName(), id))
                        .build());
    }

    @POST
    public Response add(@Valid Account account) {
        Account account1 = tradingService.createAccount(account);
        URI uri = uriInfo.getAbsolutePathBuilder().path(getClass(), "getById").build(account1.getId());
        return Response.created(uri).entity(account1).build();
    }

    @POST
    @Path("/{id}/value")
    public void addMoney(@PathParam("id") UUID id, JsonObject movement) {
        BigDecimal value = movement.getJsonNumber("value").bigDecimalValue();
        tradingService.addMoney(id, value);
    }

    @DELETE
    @Path("/{id}/value")
    public void removeMoney(@PathParam("id") UUID id, JsonObject movement) {
        BigDecimal value = movement.getJsonNumber("value").bigDecimalValue();
        tradingService.removeMoney(id, value);
    }
}
