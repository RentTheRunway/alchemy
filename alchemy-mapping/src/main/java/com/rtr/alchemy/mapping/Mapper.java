package com.rtr.alchemy.mapping;

/**
 * Defines how to map an object to a destination type
 */
public interface Mapper {
    <T> T map(Object source, Class<T> destinationType);
}
