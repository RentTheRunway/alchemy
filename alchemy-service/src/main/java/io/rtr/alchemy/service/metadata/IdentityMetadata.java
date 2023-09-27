package io.rtr.alchemy.service.metadata;

import io.rtr.alchemy.dto.identities.IdentityDto;
import io.rtr.alchemy.identities.Identity;
import io.rtr.alchemy.mapping.Mapper;

import java.util.Set;

/** Metadata for an identity type */
public class IdentityMetadata {
    private final String typeName;
    private final Class<? extends Identity> identityType;
    private final Class<? extends IdentityDto> dtoType;
    private final Class<? extends Mapper> mapperType;
    private final Set<String> attributes;

    public IdentityMetadata(
            String typeName,
            Class<? extends Identity> identityType,
            Class<? extends IdentityDto> dtoType,
            Class<? extends Mapper> mapperType) {
        this.typeName = typeName;
        this.identityType = identityType;
        this.dtoType = dtoType;
        this.mapperType = mapperType;
        this.attributes = Identity.getSupportedAttributes(identityType);
    }

    public String getTypeName() {
        return typeName;
    }

    public Class<? extends Identity> getIdentityType() {
        return identityType;
    }

    public Class<? extends IdentityDto> getDtoType() {
        return dtoType;
    }

    public Class<? extends Mapper> getMapperType() {
        return mapperType;
    }

    public Set<String> getAttributes() {
        return attributes;
    }
}
