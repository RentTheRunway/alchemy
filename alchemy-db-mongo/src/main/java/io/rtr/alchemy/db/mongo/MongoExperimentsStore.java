package io.rtr.alchemy.db.mongo;

import dev.morphia.Datastore;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Query;
import dev.morphia.query.Sort;
import dev.morphia.query.filters.Filters;

import io.rtr.alchemy.db.ExperimentsStore;
import io.rtr.alchemy.db.Filter;
import io.rtr.alchemy.db.Ordering;
import io.rtr.alchemy.db.Ordering.Direction;
import io.rtr.alchemy.db.Ordering.Field;
import io.rtr.alchemy.db.mongo.models.ExperimentEntity;
import io.rtr.alchemy.db.mongo.util.ExperimentIterable;
import io.rtr.alchemy.models.Experiment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/** A store backed by MongoDB which allows storing Experiments */
public class MongoExperimentsStore implements ExperimentsStore {
    private final Datastore ds;
    private final RevisionManager revisionManager;

    public MongoExperimentsStore(final Datastore ds, final RevisionManager revisionManager) {
        this.ds = ds;
        this.revisionManager = revisionManager;
    }

    @Override
    public void save(final Experiment experiment) {
        final ExperimentEntity entity = ExperimentEntity.from(experiment);
        entity.revision = revisionManager.nextRevision();
        ds.save(entity);
    }

    @Override
    public Experiment load(final String experimentName, final Experiment.Builder builder) {
        final ExperimentEntity entity =
                ds.find(ExperimentEntity.class).filter(Filters.eq("name", experimentName)).first();
        return entity == null ? null : entity.toExperiment(builder);
    }

    @Override
    public void delete(final String experimentName) {
        ds.find(ExperimentEntity.class).filter(Filters.eq("name", experimentName)).delete();
    }

    @Override
    public Iterable<Experiment> find(final Filter filter, final Experiment.BuilderFactory factory) {

        final Query<ExperimentEntity> query = ds.find(ExperimentEntity.class);

        if (filter.getFilter() != null) {
            query.filter(
                    Filters.or(
                            Filters.regex("name", filter.getFilter()).caseInsensitive(),
                            Filters.regex("description", filter.getFilter()).caseInsensitive()));
        }

        final FindOptions findOptions = new FindOptions();
        final Ordering ordering = filter.getOrdering();
        if (ordering != null && !ordering.isEmpty()) {
            final List<Sort> sorts = new ArrayList<>();
            for (final Entry<Field, Direction> entry : ordering.getFields().entrySet()) {
                final String field = ExperimentEntity.getFieldName(entry.getKey());

                final Sort sort =
                        entry.getValue() == Direction.DESCENDING
                                ? Sort.descending(field)
                                : Sort.ascending(field);

                sorts.add(sort);
            }

            findOptions.sort(sorts.toArray(new Sort[] {}));
        }

        if (filter.getOffset() != null) {
            findOptions.skip(filter.getOffset());
        }

        if (filter.getLimit() != null) {
            findOptions.limit(filter.getLimit());
        }

        return new ExperimentIterable(query.stream(findOptions).iterator(), factory);
    }
}
