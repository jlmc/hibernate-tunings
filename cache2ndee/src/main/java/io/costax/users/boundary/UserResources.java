package io.costax.users.boundary;

import io.costax.users.entity.User;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Path("/users")
@Produces({MediaType.APPLICATION_JSON})
public class UserResources {

    @Inject
    Users users;

    @Context
    UriInfo uriInfo;

    @GET
    public List<User> list() {
        return users.list();
    }

    @GET
    @Path("/{id: \\d+}")
    public Response getUser(@PathParam("id") Long id) {
        final User user = users.getById(id);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .header("X-reason", String.format("Resource [%s] with identifier [%d] not found", User.class.getSimpleName(), id))
                .build();
        }

        return Response.ok(user).build();
    }

    @POST
    public Response add(@Valid User user) {
        final User added = users.add(user);

        final URI uri = uriInfo.getAbsolutePathBuilder().path("{id}").build(added.getId());

        return Response.created(uri).entity(added).build();
    }


}
