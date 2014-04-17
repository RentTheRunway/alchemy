package com.rtr.alchemy.mapping;

public abstract class MapperBuilder {
    public abstract <A,B> MapperBuilder register(Class<A> source, Class<B> destination);
    public abstract Mapper build();
}
