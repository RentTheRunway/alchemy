package io.rtr.alchemy.db;

import io.rtr.alchemy.models.Experiment;

import java.util.Map;

/**
 * An interface for retrieving active experiments and treatments information, real-time. Must be
 * optimized and fast as this will be called many times, ideally caching internally as needed.
 */
public interface ExperimentsCache {
    /**
     * Return active experiments
     *
     * @return all active experiments
     */
    Map<String, Experiment> getActiveExperiments();

    /** Forces cache to reload all data from storage */
    void invalidateAll(Experiment.BuilderFactory factory);

    /** Forces cache to reload a specific experiment from storage */
    void invalidate(String experimentName, Experiment.Builder builder);

    /** Updates the cache with a newly loaded experiment */
    void update(Experiment experiment);

    /** Updates the cache with a recently deleted experiment */
    void delete(String experimentName);

    /** Checks whether any experiments are stale */
    boolean checkIfAnyStale();

    /** Checks whether a given experiment is stale */
    boolean checkIfStale(String experimentName);
}
