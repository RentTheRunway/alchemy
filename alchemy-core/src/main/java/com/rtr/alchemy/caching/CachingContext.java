package com.rtr.alchemy.caching;

import com.google.common.collect.Maps;
import com.rtr.alchemy.db.ExperimentsCache;
import com.rtr.alchemy.models.Experiment;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A context object that allows you to interact safely with the cache, preventing multiple calls to invalidate from
 * triggering redundant cache reloads and allowing the user the option to invalidate the cache asynchronously
 */
public class CachingContext implements Closeable {
    private final ExperimentsCache cache;
    private final Experiment.BuilderFactory builderFactory;
    private final AtomicBoolean lock;
    private final ExecutorService executorService;
    private final boolean ownsExecutorService;
    private final ConcurrentMap<String, AtomicBoolean> experimentLocks;

    public CachingContext(ExperimentsCache cache,
                          Experiment.BuilderFactory builderFactory,
                          ExecutorService executorService) {
        this.cache = cache;
        this.builderFactory = builderFactory;
        this.executorService = executorService != null ? executorService : Executors.newSingleThreadExecutor();
        this.ownsExecutorService = executorService == null;
        this.experimentLocks = Maps.newConcurrentMap();
        this.lock = new AtomicBoolean(false);
    }

    public CachingContext(ExperimentsCache cache,
                          Experiment.BuilderFactory builderFactory) {
        this(cache, builderFactory, null);
    }

    /**
     * Forces cache to reload all data from storage
     */
    public void invalidateAll(boolean async) {
        if (async) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    safeInvalidateAll(builderFactory);
                }
            });
        } else {
            safeInvalidateAll(builderFactory);
        }
    }

    private void safeInvalidateAll(Experiment.BuilderFactory builderFactory) {
        if (!lock.compareAndSet(false, true)) {
            return;
        }

        try {
            cache.invalidateAll(builderFactory);
        } finally {
            lock.set(false);
        }
    }

    private AtomicBoolean getExperimentLock(String experimentName) {
        final AtomicBoolean newLock = new AtomicBoolean(false);
        final AtomicBoolean prevLock = experimentLocks.putIfAbsent(experimentName, newLock);
        return prevLock != null ? prevLock : newLock;
    }

    /**
     * Forces cache to reload a specific experiment from storage
     */
    public void invalidate(final String experimentName, boolean async) {
        if (async) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    safeInvalidate(experimentName, builderFactory.createBuilder(experimentName));
                }
            });
        } else {
            safeInvalidate(experimentName, builderFactory.createBuilder(experimentName));
        }
    }

    private void safeInvalidate(String experimentName, Experiment.Builder builder) {
        final AtomicBoolean lock = getExperimentLock(experimentName);

        if (!lock.compareAndSet(false, true)) {
            return;
        }

        try {
            cache.invalidate(experimentName, builder);
        } finally {
            lock.set(false);
            experimentLocks.remove(experimentName);
        }
    }

    /**
     * Updates the cache with a newly loaded experiment
     */
    public void update(Experiment experiment) {
        cache.update(experiment);
    }

    /**
     * Updates the cache with a recently deleted experiment
     */
    public void delete(String experimentName) {
        cache.delete(experimentName);
    }

    /**
     * Checks whether any experiments are stale
     */
    public boolean checkIfAnyStale() {
        return cache.checkIfAnyStale();
    }

    /**
     * Checks whether a given experiment is stale
     */
    public boolean checkIfStale(String experimentName) {
        return cache.checkIfStale(experimentName);
    }

    @Override
    public void close() throws IOException {
        if (ownsExecutorService) {
            executorService.shutdownNow();
        }
    }
}
