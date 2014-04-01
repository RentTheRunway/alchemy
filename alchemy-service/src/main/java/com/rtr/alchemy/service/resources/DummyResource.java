package com.rtr.alchemy.service.resources;

import com.rtr.alchemy.identities.Identity;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/dummy")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DummyResource {

    @POST
    public Response identity(@Valid Identity identity) {
        return Response.ok(identity.getHash(0)).build();
    }
}
