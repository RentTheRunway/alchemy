package io.rtr.alchemy.service.resources;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import io.dropwizard.setup.Environment;
import io.rtr.alchemy.dto.identities.IdentityDto;
import io.rtr.alchemy.identities.Identity;
import io.rtr.alchemy.mapping.Mappers;
import io.rtr.alchemy.service.metadata.IdentitiesMetadata;
import io.rtr.alchemy.service.metadata.IdentityMetadata;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/** Resource for retrieving registered identity types and their schemas */
@Path("/metadata")
@Produces(MediaType.APPLICATION_JSON)
public class MetadataResource extends BaseResource {
    private static final MetadataDtoTypesMapper DTO_TYPES_MAPPER = new MetadataDtoTypesMapper();
    private final Map<String, Class<?>> identityTypesByName;
    private final IdentitiesMetadata metadata;
    private final JsonSchemaGenerator schemaGenerator;
    private final Mappers mapper;

    @Inject
    public MetadataResource(Environment environment, IdentitiesMetadata metadata, Mappers mapper) {
        this.metadata = metadata;
        this.identityTypesByName = Maps.transformValues(metadata, DTO_TYPES_MAPPER);
        this.schemaGenerator = new JsonSchemaGenerator(environment.getObjectMapper());
        this.mapper = mapper;
    }

    @GET
    @Path("/identityTypes")
    public Map<String, Class<?>> getIdentityTypes() {
        return identityTypesByName;
    }

    @GET
    @Path("/identityTypes/{identityType}/schema")
    public JsonSchema getSchema(@PathParam("identityType") String identityType)
            throws JsonMappingException {
        final Class<?> dtoType = ensureExists(identityTypesByName.get(identityType));
        return schemaGenerator.generateSchema(dtoType);
    }

    @GET
    @Path("/identityTypes/{identityType}/attributes")
    public Set<String> getAttributes(@PathParam("identityType") String identityType)
            throws JsonMappingException {
        final IdentityMetadata metadata = ensureExists(this.metadata.get(identityType));
        return metadata.getAttributes();
    }

    @POST
    @Path("/identity/attributes")
    public Map<String, Object> computeAttributes(@Valid IdentityDto request) {
        final Identity identity = ensureExists(mapper.fromDto(request, Identity.class));
        return identity.computeAttributes();
    }

    private static class MetadataDtoTypesMapper implements Function<IdentityMetadata, Class<?>> {
        @Nullable
        @Override
        public Class<?> apply(@Nullable IdentityMetadata input) {
            return input == null ? null : input.getDtoType();
        }
    }
}
