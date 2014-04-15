package com.rtr.alchemy.mapping;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * Defines a build that creates a mapper that uses the Orika framework
 */
public class OrikaMapperBuilder extends MapperBuilder {
    private final MapperFactory factory;

    public OrikaMapperBuilder() {
        factory = new
            DefaultMapperFactory
                .Builder()
                .build();
    }

    @Override
    public <A, B> MapperBuilder register(Class<A> source, Class<B> destination) {
        factory
            .classMap(source, destination)
            .byDefault()
            .customize(new CustomMapper<A, B>() {
                @Override
                public void mapBtoA(B b, A a, MappingContext context) {
                    super.mapBtoA(b, a, context);
                }
            })
            .register();

        return this;
    }

    @Override
    public Mapper build() {
        return new OrikaMapper(factory.getMapperFacade());
    }
}
