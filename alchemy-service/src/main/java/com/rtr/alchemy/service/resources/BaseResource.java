package com.rtr.alchemy.service.resources;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public abstract class BaseResource {
    protected static  <T> T ensureExists(T value) {
        if (value == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return value;
    }

    protected static Response created() {
        return Response.status(Response.Status.CREATED).build();
    }
}
