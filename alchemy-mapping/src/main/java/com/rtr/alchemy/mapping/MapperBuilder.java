package com.rtr.alchemy.mapping;

public abstract class MapperBuilder {
    public abstract MapperBuilder register(Class<?> source, Class<?> destination);
    public abstract Mapper build();
}
