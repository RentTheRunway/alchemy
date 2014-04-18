package com.rtr.alchemy.mapping;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MappersTest {

    private static interface DtoInterface { }
    private static class ParentDto implements DtoInterface { }
    private static class ChildDto extends ParentDto { }
    private static interface BoInterface { }
    private static class ParentBo implements BoInterface { }
    private static class ChildBo extends ParentBo { }

    @Test
    public void testMapSubclassesAndInterfaces() {
        final Mappers mapper = new Mappers();
        mapper.register(ChildDto.class, ChildBo.class, new Mapper() {
            @Override
            public Object toDto(Object source) {
                return new ChildDto();
            }

            @Override
            public Object fromDto(Object source) {
                return new ChildBo();
            }
        });

        assertEquals(ChildBo.class, mapper.fromDto(new ChildDto(), ChildBo.class).getClass());
        assertEquals(ChildBo.class, mapper.fromDto(new ChildDto(), ParentBo.class).getClass());
        assertEquals(ChildBo.class, mapper.fromDto(new ChildDto(), BoInterface.class).getClass());

        assertEquals(ChildDto.class, mapper.toDto(new ChildBo(), ChildDto.class).getClass());
        assertEquals(ChildDto.class, mapper.toDto(new ChildBo(), ParentDto.class).getClass());
        assertEquals(ChildDto.class, mapper.toDto(new ChildBo(), DtoInterface.class).getClass());
    }
}
