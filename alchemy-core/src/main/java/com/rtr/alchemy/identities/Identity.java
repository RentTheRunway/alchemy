package com.rtr.alchemy.identities;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Identifies a unique entity whose hash code is used for treatments allocation
 */
public abstract class Identity {
    private static final Set<String> EMPTY = Collections.unmodifiableSet(Sets.<String>newHashSet());
    private static final LoadingCache<Class<?>, Set<String>> SEGMENTS_CACHE =
        CacheBuilder
            .newBuilder()
            .build(new CacheLoader<Class<?>, Set<String>>() {
                @Override
                public Set<String> load(@Nonnull Class<?> clazz) throws Exception {
                    final Segments annotation = clazz.getAnnotation(Segments.class);
                    if (annotation == null) {
                        return EMPTY;
                    }

                    return Sets.newHashSet(annotation.value());
                }
            });

    /**
     * generates a hash code used to assign identity to treatment
     */
    public abstract long computeHash(int seed);

    /**
     * generates a list of segments that categorize this identity for filtering
     */
    public Set<String> computeSegments() {
        return EMPTY;
    }

    protected IdentityBuilder identity(int seed) {
        return IdentityBuilder.seed(seed);
    }

    /**
     * Get a list of possible segment values that can be returned by this identity
     */
    public static <T extends Identity> Set<String> getSupportedSegments(Class<T> clazz) {
        try {
            return SEGMENTS_CACHE.get(clazz);
        } catch (final ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
