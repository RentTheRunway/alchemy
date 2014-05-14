package com.rtr.alchemy.service.resources;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.rtr.alchemy.service.metadata.IdentitiesMetadata;
import com.rtr.alchemy.service.metadata.IdentityMetadata;
import io.dropwizard.setup.Environment;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

/**
 * Resource for retrieving registered identity types and their schemas
 */
@Path("/metadata")
@Produces(MediaType.APPLICATION_JSON)
public class MetadataResource extends BaseResource {
    private final Map<String, Class<?>> identityTypesByName;
    private final JsonSchemaGenerator schemaGenerator;

    @Inject
    public MetadataResource(Environment environment, IdentitiesMetadata metadata) {
        this.identityTypesByName = Maps.newHashMap();
        for (final IdentityMetadata identity : metadata.values()) {
            identityTypesByName.put(identity.getTypeName(), identity.getDtoType());
        }
        this.schemaGenerator = new JsonSchemaGenerator(environment.getObjectMapper());
    }

    @GET
    @Path("/identityTypes")
    public Map<String, Class<?>> getIdentityTypes() {
        return identityTypesByName;
    }

    @GET
    @Path("/identityTypes/{identityType}")
    public JsonSchema getSchema(@PathParam("identityType") String identityType) throws JsonMappingException {
        final Class<?> dtoType = ensureExists(identityTypesByName.get(identityType));
        return schemaGenerator.generateSchema(dtoType);
    }
}