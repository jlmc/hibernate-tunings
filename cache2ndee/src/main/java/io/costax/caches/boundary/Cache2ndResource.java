package io.costax.caches.boundary;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/cache-2nd")
public class Cache2ndResource {

    @Inject
    Cache2ndManager cache2ndManager;

    @POST
    @Path("/evict-all")
    public void evictAll() {
        cache2ndManager.evictAllCache();
    }
}
