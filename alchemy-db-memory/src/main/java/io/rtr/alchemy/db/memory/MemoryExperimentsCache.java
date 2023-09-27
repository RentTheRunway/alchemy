package io.rtr.alchemy.db.memory;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import io.rtr.alchemy.db.ExperimentsCache;
import io.rtr.alchemy.models.Experiment;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Map.Entry;

/** Implements a cache that caches experiments in memory */
public class MemoryExperimentsCache implements ExperimentsCache {
    private static final ActiveFilter ACTIVE_FILTER = new ActiveFilter();
    private static final ExperimentCopyTransformer EXPERIMENT_COPY_TRANSFORMER =
            new ExperimentCopyTransformer();
    private final Map<String, Experiment> db;

    public MemoryExperimentsCache(Map<String, Experiment> db) {
        this.db = db;
    }

    @Override
    public Map<String, Experiment> getActiveExperiments() {
        final Map<String, Experiment> filtered = Maps.filterEntries(db, ACTIVE_FILTER);
        return Maps.transformEntries(filtered, EXPERIMENT_COPY_TRANSFORMER);
    }

    @Override
    public void invalidateAll(Experiment.BuilderFactory factory) {}

    @Override
    public void invalidate(String experimentName, Experiment.Builder builder) {}

    @Override
    public void update(Experiment experiment) {}

    @Override
    public void delete(String experimentName) {}

    @Override
    public boolean checkIfAnyStale() {
        return false;
    }

    @Override
    public boolean checkIfStale(String experimentName) {
        return false;
    }

    private static class ActiveFilter implements Predicate<Entry<String, Experiment>> {
        @Override
        public boolean apply(@Nullable Entry<String, Experiment> input) {
            return input != null && input.getValue().isActive();
        }
    }

    private static class ExperimentCopyTransformer
            implements Maps.EntryTransformer<String, Experiment, Experiment> {
        @Override
        public Experiment transformEntry(@Nullable String key, @Nullable Experiment value) {
            return value == null ? null : Experiment.copyOf(value);
        }
    }
}
