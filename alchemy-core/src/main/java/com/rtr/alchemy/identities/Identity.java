package com.rtr.alchemy.identities;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Identifies a unique entity whose hash code is used for treatments allocation
 */
public abstract class Identity {
    protected static final Set<String> EMPTY = Collections.unmodifiableSet(Sets.<String>newHashSet());
    private static final LoadingCache<Class<?>, Set<String>> ATTRIBUTES_CACHE =
        CacheBuilder
            .newBuilder()
            .build(new CacheLoader<Class<?>, Set<String>>() {
                @Override
                public Set<String> load(@Nonnull Class<?> clazz) throws Exception {
                    final Attributes annotation = clazz.getAnnotation(Attributes.class);
                    if (annotation == null) {
                        return EMPTY;
                    }

                    final Set<String> result = Sets.newHashSet();
                    Collections.addAll(result, annotation.value());

                    for (final Class<? extends Identity> identity : annotation.identities()) {
                        result.addAll(ATTRIBUTES_CACHE.get(identity));
                    }

                    return result;
                }
            });

    /**
     * generates a hash code used to assign identity to treatment
     * @param seed a seed value to randomize the resulting hash from experiment to experiment for the same identity
     * @param hashAttributes a set of attributes that should be used to compute the hash code
     * @param attributes a map of attribute values
     */
    public long computeHash(int seed, LinkedHashSet<String> hashAttributes, AttributesMap attributes) {
        final IdentityBuilder builder = IdentityBuilder.seed(seed);
        final Iterable<String> names = hashAttributes.isEmpty() ? attributes.keySet() : hashAttributes;

        for (String name : names) {
            final Class<?> type = attributes.getType(name);

            if (type == String.class) {
                builder.putString(attributes.getString(name));
            } else if (type == Long.class) {
                builder.putLong(attributes.getNumber(name));
            } else if (type == Boolean.class) {
                builder.putBoolean(attributes.getBoolean(name));
            }
        }

        return builder.hash();
    }

    /**
     * generates a list of attributes that describe this identity for filtering
     */
    public abstract AttributesMap computeAttributes();

    /**
     * Convenience method for getting an identity builder given a seed
     */
    protected IdentityBuilder identity(int seed) {
        return IdentityBuilder.seed(seed);
    }

    /**
     * Convenience method for getting an attributes map builder
     */
    protected AttributesMap.Builder attributes() {
        return AttributesMap.newBuilder();
    }

    /**
     * Get a list of possible attribute values that can be returned by this identity
     */
    public static <T extends Identity> Set<String> getSupportedAttributes(Class<T> clazz) {
        try {
            return ATTRIBUTES_CACHE.get(clazz);
        } catch (final ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
