package com.rtr.alchemy.db;

import com.rtr.alchemy.models.Experiment;

import java.util.Map;

/**
 * An interface for retrieving active experiments and treatments information, real-time.  Must be optimized and fast as this
 * will be called many times, ideally caching internally as needed.
 */
public interface ExperimentsCache {
    /**
     * Return active experiments
     * @return all active experiments
     */
    public abstract Map<String, Experiment> getActiveExperiments();

    /**
     * Forces cache to reload all data from storage
     */
    public abstract void invalidate();

    /**
     * Disposes any resources this object may be using
     */
    public abstract void close();
}
