package com.rtr.alchemy.caching;

import com.rtr.alchemy.models.Experiment;

/**
 * Defines the behavior for when to invalidate items in the cache
 */
public interface CacheStrategy {
    /**
     * Fires when an experiment is loaded from the store
     */
    void onLoad(Experiment experiment, CachingContext context);

    /**
     * Fires when an experiment is saved to the store
     */
    void onSave(Experiment experiment, CachingContext context);

    /**
     * Fires when an experiment is deleted from the store
     */
    void onDelete(String experimentName, CachingContext context);

    /**
     * Fires before an experiment is read from the cache
     */
    void onCacheRead(String experimentName, CachingContext context);

    /**
     * Fires before all experiments are read from the cache
     */
    void onCacheRead(CachingContext context);
}
