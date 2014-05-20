package com.rtr.alchemy.service.metadata;

import com.rtr.alchemy.dto.identities.IdentityDto;
import com.rtr.alchemy.identities.Identity;
import com.rtr.alchemy.mapping.Mapper;

import java.util.Set;

/**
 * Metadata for an identity type
 */
public class IdentityMetadata {
    private final String typeName;
    private final Class<? extends Identity> identityType;
    private final Class<? extends IdentityDto> dtoType;
    private final Class<? extends Mapper> mapperType;
    private final Set<String> segments;

    public IdentityMetadata(String typeName,
                            Class<? extends Identity> identityType,
                            Class<? extends IdentityDto> dtoType,
                            Class<? extends Mapper> mapperType) {
        this.typeName = typeName;
        this.identityType = identityType;
        this.dtoType = dtoType;
        this.mapperType = mapperType;
        this.segments = Identity.getSupportedSegments(identityType);
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

    public Set<String> getSegments() {
        return segments;
    }
}