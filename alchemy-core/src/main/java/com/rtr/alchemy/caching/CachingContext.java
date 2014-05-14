package com.rtr.alchemy.caching;

import com.google.common.collect.Maps;
import com.rtr.alchemy.db.ExperimentsCache;
import com.rtr.alchemy.models.Experiment;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A context object that allows you to interact safely with the cache, preventing multiple calls to invalidate from
 * triggering redundant cache reloads and allowing the user the option to invalidate the cache asynchronously
 */
public class CachingContext implements Closeable {
    private final ExperimentsCache cache;
    private final Experiment.BuilderFactory builderFactory;
    private final ReentrantLock lock;
    private final ExecutorService executorService;
    private final boolean ownsExecutorService;
    private final ConcurrentMap<String, ReentrantLock> experimentLocks;

    public CachingContext(ExperimentsCache cache,
                          Experiment.BuilderFactory builderFactory,
                          ExecutorService executorService) {
        this.cache = cache;
        this.builderFactory = builderFactory;
        this.executorService = executorService != null ? executorService : Executors.newSingleThreadExecutor();
        this.ownsExecutorService = executorService == null;
        this.experimentLocks = Maps.newConcurrentMap();
        this.lock = new ReentrantLock();
    }

    /**
     * Forces cache to reload all data from storage
     */
    public void invalidateAll(boolean async) {
        if (!lock.tryLock()) {
            return;
        }

        try {
            if (async) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        cache.invalidateAll(builderFactory);
                    }
                });
            } else {
                cache.invalidateAll(builderFactory);
            }
        } finally {
            lock.unlock();
        }
    }

    private ReentrantLock getExperimentLock(String experimentName) {
        final ReentrantLock newLock = new ReentrantLock();
        final ReentrantLock prevLock = experimentLocks.putIfAbsent(experimentName, newLock);
        return prevLock != null ? prevLock : newLock;
    }

    /**
     * Forces cache to reload a specific experiment from storage
     */
    public void invalidate(final String experimentName, boolean async) {
        final ReentrantLock lock = getExperimentLock(experimentName);

        if (!lock.tryLock()) {
            return;
        }

        try {
            if (async) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        cache.invalidate(experimentName, builderFactory.createBuilder(experimentName));
                    }
                });
            } else {
                cache.invalidate(experimentName, builderFactory.createBuilder(experimentName));
            }
        } finally {
            lock.unlock();
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
