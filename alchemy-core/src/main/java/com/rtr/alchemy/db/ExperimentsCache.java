package com.rtr.alchemy.db;

import java.util.Map;

/**
 * An interface for retrieving active experiments and treatments information, real-time.  Must be optimized and fast as this
 * will be called many times, ideally caching internally as needed.
 */
public interface ExperimentsCache {
    /**
     * Returns whether an experiment is active
     * @param experiment The experiment to test
     * @return true if experiment is active, otherwise false
     */
    boolean isExperimentActive(String experiment);


    /**
     * Returns the currently assigned treatment
     * @param bin The bin to return assigned treatment for
     * @param experiment The experiment to return treatment for
     * @return The currently allocated treatment for a given bin
     */
    String getActiveTreatment(int bin, String experiment);

    /**
     * Returns assigned treatments for a given bin
     * @param bin The bin to return map of treatments for
     * @return A map of experiment -> treatment, null treatment means unallocated
     */
    Map<String, String> getActiveTreatments(int bin);
}
