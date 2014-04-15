package com.rtr.alchemy.mapping;

import ma.glasnost.orika.MapperFacade;

/**
 * Defines a mapper that uses the Orika framework to do object mapping
 */
public class OrikaMapper implements Mapper {
    private final MapperFacade mapper;

    public OrikaMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }

    @Override
    public <T> T map(Object source, Class<T> destinationType) {
        return mapper.map(source, destinationType);
    }
}
