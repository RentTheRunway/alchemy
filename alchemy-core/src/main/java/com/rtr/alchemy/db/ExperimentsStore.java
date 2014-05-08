package com.rtr.alchemy.db;

import com.rtr.alchemy.models.Experiment;

/**
 * An interface for defining basic CRUD operations around experiments, treatments and allocations.  These operations do
 * not need to be highly optimized or fast
 */
public interface ExperimentsStore {
    /**
     * Save an experiment, creating or updating it
     * @param experiment The experiment to create or update
     */
    void save(Experiment experiment);

    /**
     * Retrieves an experiment
     * @param experimentName The name of the experiment
     * @param builder The builder to use to construct the experiment
     * @return The experiment with the given name
     */
    Experiment load(String experimentName, Experiment.Builder builder);

    /**
     * Deletes an experiment and its associated data
     * @param experimentName The name of the experiment
     */
    void delete(String experimentName);

    /**
     * Finds experiments with given criteria
     * @param filter Criteria for pagination and filtering
     * @return Filtered list of experiments
     */
    Iterable<Experiment> find(Filter filter, Experiment.BuilderFactory factory);
}
