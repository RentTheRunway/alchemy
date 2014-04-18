package com.rtr.alchemy.mapping;

/**
 * Defines how to map an object to and from DTO and business object
 */
public interface Mapper<TDto, TBo> {
    TDto toDto(TBo source);
    TBo fromDto(TDto source);
}
