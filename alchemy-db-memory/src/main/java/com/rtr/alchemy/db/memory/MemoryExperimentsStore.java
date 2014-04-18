package com.rtr.alchemy.db.memory;

import com.google.common.collect.Lists;
import com.rtr.alchemy.db.ExperimentsStore;
import com.rtr.alchemy.db.Filter;
import com.rtr.alchemy.models.Experiment;

import java.util.List;

public class MemoryExperimentsStore implements ExperimentsStore {
    private final MemoryDatabase db;

    public MemoryExperimentsStore(MemoryDatabase db) {
        this.db = db;
    }

    @Override
    public void save(Experiment experiment) {
        synchronized (db) {
            db.getExperiments().put(experiment.getName(), experiment);
        }
    }

    @Override
    public Experiment load(String experimentName, Experiment.Builder builder) {
        synchronized (db) {
            return db.getExperiments().get(experimentName);
        }
    }

    @Override
    public void delete(String experimentName) {
        synchronized (db) {
            db.getExperiments().remove(experimentName);
        }
    }

    private static boolean filterMatches(String filter, Object ... values) {
        if (filter == null || filter.isEmpty()) {
            return true;
        }

        for (Object obj : values) {
            if (obj == null) {
                continue;
            }

            final String value = String.valueOf(obj);
            if (value.toLowerCase().contains(filter.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Iterable<Experiment> find(Filter filter) {
        int limit = 0;
        int offset = 0;
        final List<Experiment> result = Lists.newArrayList();

        synchronized (db) {
            for (Experiment experiment : db.getExperiments().values()) {
                if (filter.getOffset() != null && offset++ < filter.getOffset()) {
                    continue;
                }

                if (filterMatches(
                    filter.getFilter(),
                    experiment.getName(),
                    experiment.getDescription()
                )) {
                    if (filter.getLimit() != null && ++limit > filter.getLimit()) {
                        break;
                    }

                    result.add(experiment);
                }
            }
        }

        return result;
    }

    @Override
    public void close() {
    }
}
