package com.rtr.alchemy.db.memory;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.rtr.alchemy.db.ExperimentsCache;
import com.rtr.alchemy.models.Experiment;

import java.util.Map;

public class MemoryExperimentsCache implements ExperimentsCache {
    private final Map<String, Experiment> db;

    public MemoryExperimentsCache(Map<String, Experiment> db) {
        this.db = db;
    }

    @Override
    public Map<String, Experiment> getActiveExperiments() {
        final Map<String, Experiment> filtered = Maps.filterEntries(
            db,
            new Predicate<Map.Entry<String, Experiment>>() {
                @Override
                public boolean apply(Map.Entry<String, Experiment> entry) {
                    return entry.getValue().isActive();
                }
            }
        );

        // copy each experiment
        return Maps.transformEntries(filtered, new Maps.EntryTransformer<String, Experiment, Experiment>() {
            @Override
            public Experiment transformEntry(String key, Experiment value) {
                return Experiment.copyOf(value);
            }
        });
    }

    @Override
    public void invalidateAll(Experiment.BuilderFactory factory) {
    }

    @Override
    public void invalidate(String experimentName, Experiment.Builder builder) {
    }

    @Override
    public void update(Experiment experiment) {
    }

    @Override
    public void delete(String experimentName) {
    }

    @Override
    public boolean checkIfAnyStale() {
        return false;
    }

    @Override
    public boolean checkIfStale(String experimentName) {
        return false;
    }
}
