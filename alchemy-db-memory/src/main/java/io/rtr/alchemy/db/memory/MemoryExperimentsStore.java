package io.rtr.alchemy.db.memory;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import io.rtr.alchemy.db.ExperimentsStore;
import io.rtr.alchemy.db.Filter;
import io.rtr.alchemy.db.Ordering;
import io.rtr.alchemy.db.Ordering.Field;
import io.rtr.alchemy.db.Ordering.Direction;
import io.rtr.alchemy.models.Experiment;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

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

    private static class OrderingComparator implements Comparator<Experiment> {
        private final Ordering ordering;

        private OrderingComparator(Ordering ordering) {
            this.ordering = ordering;
        }

        @Override
        public int compare(Experiment a, Experiment b) {
            ComparisonChain chain = ComparisonChain.start();

            for (Entry<Field, Direction> entry : ordering.getFields().entrySet()) {
                final Ordering.Field field = entry.getKey();
                final Experiment lhs = entry.getValue() == Ordering.Direction.ASCENDING ? a : b;
                final Experiment rhs = entry.getValue() == Ordering.Direction.ASCENDING ? b : a;

                switch (field) {
                    case NAME:
                        chain = chain.compare(lhs.getName(), rhs.getName());
                        break;

                    case DESCRIPTION:
                        chain = chain.compare(lhs.getDescription(), rhs.getDescription());
                        break;

                    case ACTIVE:
                        chain = chain.compare(lhs.isActive(), rhs.isActive());
                        break;

                    case CREATED:
                        chain = chain.compare(lhs.getCreated(), rhs.getCreated());
                        break;

                    case MODIFIED:
                        chain = chain.compare(lhs.getModified(), rhs.getModified());
                        break;

                    case ACTIVATED:
                        chain = chain.compare(lhs.getActivated(), rhs.getActivated());
                        break;

                    case DEACTIVATED:
                        chain = chain.compare(lhs.getDeactivated(), rhs.getDeactivated());
                        break;

                    default:
                        throw new IllegalArgumentException(
                            String.format(
                                "Unsupported ordering field: %s (%s)",
                                field,
                                field.getName()
                            )
                        );
                }
            }

            return chain.result();
        }
    }

    @Override
    public Iterable<Experiment> find(Filter filter, Experiment.BuilderFactory factory) {
        final List<Experiment> result = Lists.newArrayList();

        for (final Experiment experiment : db.values()) {
            if (filterMatches(
                filter.getFilter(),
                experiment.getName(),
                experiment.getDescription())) {
                result.add(Experiment.copyOf(experiment));
            }
        }

        if (filter.getOrdering() != null && !filter.getOrdering().isEmpty()) {
            final Comparator<Experiment> comparator = new OrderingComparator(filter.getOrdering());
            Collections.sort(result, comparator);
        }

        Iterable<Experiment> filteredResult = result;

        if (filter.getOffset() != null) {
            filteredResult = Iterables.skip(filteredResult, filter.getOffset());
        }

        if (filter.getLimit() != null) {
            filteredResult = Iterables.limit(filteredResult, filter.getLimit());
        }

        return Lists.newArrayList(filteredResult);
    }
}
