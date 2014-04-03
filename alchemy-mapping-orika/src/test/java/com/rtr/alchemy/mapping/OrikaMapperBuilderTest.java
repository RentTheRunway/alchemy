package com.rtr.alchemy.mapping;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class OrikaMapperBuilderTest {
    public static class FooDto {
        private final String name;

        public FooDto(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class BarDto extends FooDto {
        private final int value;

        public BarDto(String name, int value) {
            super(name);
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static class Foo {
        private final String name;

        public Foo(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class Bar extends Foo {
        private int value;

        public Bar(String name, int value) {
            super(name);
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    @Test
    public void testMappingWithoutRegister() {
        Mapper mapper = new OrikaMapperBuilder().build();
        Foo foo = mapper.map(new BarDto("bar", 1), Foo.class);
        assertNotEquals("should not have been mapped to unregistered type", Bar.class, foo.getClass());
        assertEquals("base class value should have been mapped", "bar", foo.getName());
    }

    @Test
    public void testMapping() {
        MapperBuilder builder = new OrikaMapperBuilder();
        builder = builder.register(BarDto.class, Bar.class);
        Mapper mapper = builder.build();

        Foo foo = mapper.map(new BarDto("bar", 1), Foo.class);
        assertEquals("should have been mapped to registered type", Bar.class, foo.getClass());

        Bar bar = (Bar) foo;
        assertEquals("value should have been mapped", "bar", bar.getName());
        assertEquals("value should have been mapped", 1, bar.getValue());
    }
}
