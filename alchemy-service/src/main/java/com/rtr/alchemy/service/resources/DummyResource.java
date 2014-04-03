package com.rtr.alchemy.service.resources;

import com.google.inject.Inject;
import com.rtr.alchemy.identities.Identity;
import com.rtr.alchemy.dto.identities.IdentityDto;
import com.rtr.alchemy.mapping.Mapper;

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
    private final Mapper mapper;

    @Inject
    public DummyResource(Mapper mapper) {
        this.mapper = mapper;
    }

    @POST
    public Response identity(@Valid IdentityDto dto) {
        Identity identity = mapper.map(dto, Identity.class);
        return Response.ok(identity.getHash(0)).build();
    }
}
