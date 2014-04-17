package com.rtr.alchemy.mapping;

import com.rtr.alchemy.dto.identities.IdentityDto;
import com.rtr.alchemy.identities.Identity;
import org.junit.Before;

import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.mock;

/**
 * The purpose of this class is to verify that IdentityDto types can be mapped to and from without exceptions being thrown
 */
public class MappingTestBase {
    private Mapper mapper;
    private MapperBuilder mapperBuilder;

    @Before
    public void setUp() {
        mapperBuilder = new OrikaMapperBuilder();
    }

    protected void register(Class<?> from, Class<?> to) {
        mapperBuilder = mapperBuilder.register(from, to);
        mapperBuilder = mapperBuilder.register(to, from);
    }

    protected void configure() {
        mapper = mapperBuilder.build();
    }

    protected void testMapping(Class<?> dtoClazz, Class<?> clazz) {
        assertNotNull("you must call configure() first", mapper);
        final Object dto = mock(dtoClazz);
        mapper.map(dto, clazz);
        mapper.map(dto, Identity.class);

        final Object obj = mock(clazz);
        mapper.map(obj, dtoClazz);
        mapper.map(obj, IdentityDto.class);
    }
}
