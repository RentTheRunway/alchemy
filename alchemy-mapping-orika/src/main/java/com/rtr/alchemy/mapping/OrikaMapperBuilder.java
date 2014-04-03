package com.rtr.alchemy.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * Defines a build that creates a mapper that uses the Orika framework
 */
public class OrikaMapperBuilder extends MapperBuilder {
    private final MapperFactory factory;

    public OrikaMapperBuilder() {
        factory = new DefaultMapperFactory.Builder().build();
    }

    @Override
    public MapperBuilder register(Class<?> source, Class<?> destination) {
        factory
            .classMap(source, destination)
            .byDefault()
            .register();

        return this;
    }

    @Override
    public Mapper build() {
        return new OrikaMapper(factory.getMapperFacade());
    }
}
