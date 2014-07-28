package io.rtr.alchemy.db.memory;

import com.google.common.collect.Lists;
import io.rtr.alchemy.db.ExperimentsStore;
import io.rtr.alchemy.db.Filter;
import io.rtr.alchemy.models.Experiment;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Implements a store that stores experiments in memory
 */
public class MemoryExperimentsStore implements ExperimentsStore {
    private final Map<String, Experiment> db;

    public MemoryExperimentsStore(Map<String, Experiment> db) {
        this.db = db;
    }

    @Override
    public void save(Experiment experiment) {
        db.put(experiment.getName(), Experiment.copyOf(experiment));
    }

    @Override
    public Experiment load(String experimentName, Experiment.Builder builder) {
        return Experiment.copyOf(db.get(experimentName));
    }

    @Override
    public void delete(String experimentName) {
        db.remove(experimentName);
    }

    private static boolean filterMatches(String filter, Object ... values) {
        if (filter == null || filter.isEmpty()) {
            return true;
        }

        for (final Object obj : values) {
            if (obj == null) {
                continue;
            }

            final String value = String.valueOf(obj).toLowerCase(Locale.getDefault());
            final String other = filter.toLowerCase(Locale.getDefault());
            if (value.contains(other)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Iterable<Experiment> find(Filter filter, Experiment.BuilderFactory factory) {
        int limit = 0;
        int offset = 0;
        final List<Experiment> result = Lists.newArrayList();

        for (final Experiment experiment : db.values()) {
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

                result.add(Experiment.copyOf(experiment));
            }
        }

        return result;
    }
}
