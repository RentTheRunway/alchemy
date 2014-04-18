package com.rtr.alchemy.dto.identities;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Represents an identity which other identity DTOs must extend
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public abstract class IdentityDto {
}
