package io.rtr.alchemy.caching;

import io.rtr.alchemy.models.Experiment;

/**
 * Implements a basic cache strategy that will always update the cache any time an experiment is loaded, saved, or
 * deleted
 */
public class BasicCacheStrategy implements CacheStrategy {
    private static void updateOrDelete(Experiment experiment, CachingContext context) {
        if (!experiment.isActive()) {
            context.delete(experiment.getName());
        } else {
            context.update(experiment);
        }
    }

    @Override
    public void onLoad(Experiment experiment, CachingContext context) {
        updateOrDelete(experiment, context);
    }

    @Override
    public void onSave(Experiment experiment, CachingContext context) {
        updateOrDelete(experiment, context);
    }

    @Override
    public void onDelete(String experimentName, CachingContext context) {
        context.delete(experimentName);
    }

    @Override
    public void onCacheRead(String experimentName, CachingContext context) {
    }

    @Override
    public void onCacheRead(CachingContext context) {
    }
}
