package com.rtr.alchemy.db.memory;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.rtr.alchemy.db.ExperimentsCache;
import com.rtr.alchemy.models.Experiment;

import java.util.Map;

public class MemoryExperimentsCache implements ExperimentsCache {
    private final MemoryDatabase db;

    public MemoryExperimentsCache(MemoryDatabase db) {
        this.db = db;
    }

    @Override
    public Map<String, Experiment> getActiveExperiments() {
        synchronized (db) {
            final Map<String, Experiment> filtered = Maps.filterEntries(
                db.getExperiments(),
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
    }

    @Override
    public void invalidate() {
    }

    @Override
    public void close() {
    }
}
