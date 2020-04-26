package io.costax.trading.finex.entity;

import javax.ws.rs.QueryParam;

public class SearchFilter {

    @QueryParam("owner")
    private String owner;

    public String getOwner() {
        return owner;
    }
}
