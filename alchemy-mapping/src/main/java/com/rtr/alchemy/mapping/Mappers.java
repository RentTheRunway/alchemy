package com.rtr.alchemy.mapping;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * The main class for registering mappers and doing mapping
 */
public class Mappers {
    private final Map<Class<?>, Map<Class<?>, Mapper>> mappers = Maps.newConcurrentMap();

    public void register(Class<?> dtoType, Class<?> boType, Mapper mapper) {
        registerMapper(dtoType, boType, mapper);
        registerMapper(boType, dtoType, mapper);
    }

    private void registerMapper(Class<?> sourceType, Class<?> destType, Mapper mapper) {
        Map<Class<?>, Mapper> bySourceType = mappers.get(sourceType);

        if (bySourceType == null) {
            bySourceType = Maps.newConcurrentMap();
            mappers.put(sourceType, bySourceType);
        }

        Class<?> targetType = destType;
        final Set<Class<?>> interfaces = Sets.newHashSet();

        while (!targetType.equals(Object.class)) {
            bySourceType.put(targetType, mapper);
            interfaces.addAll(Arrays.asList(targetType.getInterfaces()));
            targetType = targetType.getSuperclass();
        }

        for (final Class<?> ifaceType : interfaces) {
            bySourceType.put(ifaceType, mapper);
        }
    }

    private Mapper findMapper(Class<?> sourceType, Class<?> destType) {
        Class<?> mappableType = sourceType;
        Map<Class<?>, Mapper> bySourceType = mappers.get(mappableType);

        while (bySourceType == null && !mappableType.equals(Object.class)) {
            mappableType = mappableType.getSuperclass();
            bySourceType = mappers.get(mappableType);
        }

        Preconditions.checkNotNull(
            bySourceType,
            "No mapper defined from type %s to type %s",
            sourceType,
            destType
        );

        final Mapper mapper = bySourceType.get(destType);

        Preconditions.checkState(
            mapper != null,
            "No mapper defined from type %s to type %s",
            sourceType,
            destType
        );

        return mapper;
    }

    public <T> T toDto(Object source, Class<T> destinationType) {
        if (source == null) {
            return null;
        }

        @SuppressWarnings("unchecked")
        final Mapper<T, Object> mapper = findMapper(source.getClass(), destinationType);
        return mapper.toDto(source);
    }

    public <T> T fromDto(Object source, Class<T> destinationType) {
        if (source == null) {
            return null;
        }

        @SuppressWarnings("unchecked")
        final Mapper<Object, T> mapper = findMapper(source.getClass(), destinationType);
        return mapper.fromDto(source);
    }

    public <T> Iterable<T> toDto(Iterable<?> source, Class<T> desinationType) {
        final ImmutableList.Builder<T> builder = ImmutableList.builder();
        for (final Object item : source) {
            builder.add(toDto(item, desinationType));
        }

        return builder.build();
    }

    public <T> Iterable<T> fromDto(Iterable<?> source, Class<T> desinationType) {
        final ImmutableList.Builder<T> builder = ImmutableList.builder();
        for (final Object item : source) {
            builder.add(fromDto(item, desinationType));
        }

        return builder.build();
    }
}
