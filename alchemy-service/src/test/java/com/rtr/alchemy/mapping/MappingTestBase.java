package com.rtr.alchemy.mapping;

import org.junit.Before;

import static org.junit.Assert.assertNotNull;

/**
 * The purpose of this class is to verify that types can be mapped to and from without exceptions being thrown
 */
public class MappingTestBase {
    private Mapper mapper;
    private MapperBuilder mapperBuilder;

    @Before
    public void setUp() {
        mapperBuilder = new OrtikaMapperBuilder();
    }

    protected void register(Class<?> from, Class<?> to) {
        mapperBuilder = mapperBuilder.register(from, to);
    }

    protected void configure() {
        mapper = mapperBuilder.build();
    }

    protected void testMapping(Object value, Class<?> clazz, Class<?> baseClazz) {
        assertNotNull("you must call configure() first", mapper);
        mapper.map(value, clazz);
        mapper.map(value, baseClazz);
    }
}
