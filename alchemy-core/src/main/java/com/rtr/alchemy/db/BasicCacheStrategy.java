package com.rtr.alchemy.db;

import com.rtr.alchemy.models.Experiment;

/**
 * Implements a basic cache strategy that will always update the cache any time an experiment is loaded, saved, or
 * deleted
 */
public class BasicCacheStrategy implements CacheStrategy {
    private static void updateOrDelete(Experiment experiment, ExperimentsCache cache) {
        if (!experiment.isActive()) {
            cache.delete(experiment.getName());
        } else {
            cache.update(experiment);
        }
    }

    @Override
    public void onLoad(Experiment experiment, ExperimentsCache cache) {
        updateOrDelete(experiment, cache);
    }

    @Override
    public void onSave(Experiment experiment, ExperimentsCache cache) {
        updateOrDelete(experiment, cache);
    }

    @Override
    public void onDelete(String experimentName, ExperimentsCache cache) {
        cache.delete(experimentName);
    }

    @Override
    public void onCacheRead(String experimentName, ExperimentsCache cache) {
    }

    @Override
    public void onCacheRead(ExperimentsCache cache) {
    }
}
