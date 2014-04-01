package com.rtr.alchemy.db;

import com.rtr.alchemy.models.Allocation;
import com.rtr.alchemy.models.Experiment;
import com.rtr.alchemy.models.Treatment;

/**
 * An interface for defining basic CRUD operations around experiments, treatments and allocations.  These operations do
 * not need to be highly optimized or fast
 */
public interface ExperimentsStore {
    /**
     * Create or update an experiment
     * @param experiment The experiment to create or update
     */
    void putExperiment(Experiment experiment);

    /**
     * Retrieves an experiment
     * @param name The name of the experiment
     * @return The experiment with the given name
     */
    Experiment getExperiment(String name);

    /**
     * Removes an experiment and its treatments
     * @param name The name of the experiment
     */
    void removeExperiment(String name);

    /**
     * Retrieves experiments
     * @param filter Criteria for pagination and filtering
     * @return Filtered list of experiments
     */
    Iterable<Experiment> getExperiments(Filter filter);

    /**
     * Adds a treatment to an experiment
     * @param experiment The name of the experiment to add treatment to
     * @param treatment The treatment to add
     */
    void addTreatment(String experiment, Treatment treatment);

    /**
     * Retrieves a treatment from an experiment
     * @param experiment The name of the experiment to retrieve treatment from
     * @param name The name of the treatment to retrieve
     */
    void getTreatment(String experiment, String name);

    /**
     * Removes a treatment from an experiment
     * @param experiment The name of the experiment to remove treatment from
     * @param name The name of the treatment to remove
     */
    void removeTreatment(String experiment, String name);

    /**
     * Retrieves treatments
     * @param experiment The name of the experiment to retrieve treatments from
     * @param filter Criteria for pagination and filtering
     * @return Filtered list of treatments for given experiment
     */
    Iterable<Treatment> getTreatments(String experiment, Filter filter);

    /**
     * Retrieves current allocation of treatments for an experiment
     * @param experiment The experiment to retrieve allocations for
     * @return List of allocations
     */
    Iterable<Allocation> getAllocations(String experiment);

    /**
     * Set current allocation of treatments for an experiment
     * @param experiment The experiment to set allocations for
     * @param allocations The allocations
     */
    void setAllocations(String experiment, Iterable<Allocation> allocations);
}
