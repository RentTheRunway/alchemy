package io.rtr.alchemy.db.mongo;

import io.rtr.alchemy.db.ExperimentsStore;
import io.rtr.alchemy.db.Filter;
import io.rtr.alchemy.db.mongo.models.ExperimentEntity;
import io.rtr.alchemy.db.mongo.util.ExperimentIterable;
import io.rtr.alchemy.models.Experiment;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.query.Query;

/**
 * A store backed by MongoDB which allows storing Experiments
 */
public class MongoExperimentsStore implements ExperimentsStore {
    private final AdvancedDatastore ds;
    private final RevisionManager revisionManager;

    public MongoExperimentsStore(AdvancedDatastore ds, RevisionManager revisionManager) {
        this.ds = ds;
        this.revisionManager = revisionManager;
    }

    @Override
    public void save(Experiment experiment) {
        final ExperimentEntity entity = ExperimentEntity.from(experiment);
        entity.revision = revisionManager.nextRevision();
        ds.save(entity);
    }

    @Override
    public Experiment load(String experimentName, Experiment.Builder builder) {
        final ExperimentEntity entity = ds.get(ExperimentEntity.class, experimentName);
        return
            entity == null ?
                null :
                entity.toExperiment(builder);
    }

    @Override
    public void delete(String experimentName) {
        ds.delete(ExperimentEntity.class, experimentName);
    }

    @Override
    public Iterable<Experiment> find(Filter filter, Experiment.BuilderFactory factory) {

        final Query<ExperimentEntity> query = ds.find(ExperimentEntity.class);

        if (filter.getFilter() != null) {
            query.or(
                query.criteria("name").containsIgnoreCase(filter.getFilter()),
                query.criteria("description").containsIgnoreCase(filter.getFilter())
            );
        }

        if (filter.getOffset() != null) {
            query.offset(filter.getOffset());
        }

        if (filter.getLimit() != null) {
            query.limit(filter.getLimit());
        }

        return new ExperimentIterable(query.iterator(), factory);
    }
}
