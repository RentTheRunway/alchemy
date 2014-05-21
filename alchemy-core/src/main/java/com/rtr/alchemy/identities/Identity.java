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
    protected static final Set<String> EMPTY = Collections.unmodifiableSet(Sets.<String>newHashSet());
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

                    final Set<String> result = Sets.newHashSet();
                    Collections.addAll(result, annotation.value());

                    for (final Class<? extends Identity> identity : annotation.identities()) {
                        result.addAll(SEGMENTS_CACHE.get(identity));
                    }

                    return result;
                }
            });

    /**
     * generates a hash code used to assign identity to treatment
     * @param seed a seed value to randomize the resulting hash from experiment to experiment for the same identity
     * @param segments a set of segment values that are being expected for a given experiment that the hash is being computed for
     */
    public abstract long computeHash(int seed, Set<String> segments);

    /**
     * generates a list of segments that categorize this identity for filtering
     */
    public Set<String> computeSegments() {
        return EMPTY;
    }

    protected IdentityBuilder identity(int seed) {
        return IdentityBuilder.seed(seed);
    }

    protected Set<String> segments(String ... segments) {
        final Set<String> result = Sets.newHashSet();

        for (final String segment : segments) {
            if (segment != null) {
                result.add(segment);
            }
        }

        return result;
    }

    protected Set<String> segments(Identity ... identities) {
        final Set<String> result = Sets.newHashSet();
        for (final Identity identity : identities) {
            if (identity != null) {
                result.addAll(identity.computeSegments());
            }
        }
        return result;
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
