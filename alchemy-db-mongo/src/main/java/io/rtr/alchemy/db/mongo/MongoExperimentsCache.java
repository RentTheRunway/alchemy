package io.rtr.alchemy.db.mongo;

import com.google.common.collect.Maps;

import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;

import io.rtr.alchemy.db.ExperimentsCache;
import io.rtr.alchemy.db.mongo.models.ExperimentEntity;
import io.rtr.alchemy.models.Experiment;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/** A cache backed by MongoDB which allows for quick cached access to Experiments */
public class MongoExperimentsCache implements ExperimentsCache {
    private final RevisionManager revisionManager;
    private final Datastore ds;
    private volatile Map<String, Experiment> cachedExperiments;

    public MongoExperimentsCache(final Datastore ds, final RevisionManager revisionManager) {
        this.ds = ds;
        this.revisionManager = revisionManager;
    }

    @Override
    public void invalidateAll(final Experiment.BuilderFactory factory) {
        final Iterator<ExperimentEntity> iterator =
                ds.find(ExperimentEntity.class).filter(Filters.eq("active", true)).stream()
                        .iterator();

        final Map<String, Experiment> newMap = Maps.newConcurrentMap();
        Long maxRevision = null;

        while (iterator.hasNext()) {
            final ExperimentEntity entity = iterator.next();

            if (maxRevision == null || entity.revision > maxRevision) {
                maxRevision = entity.revision;
            }

            newMap.put(entity.name, entity.toExperiment(factory.createBuilder(entity.name)));
        }

        if (maxRevision != null) {
            revisionManager.setLatestRevision(maxRevision);
        }
        cachedExperiments = newMap;
    }

    @Override
    public Map<String, Experiment> getActiveExperiments() {
        return Collections.unmodifiableMap(cachedExperiments);
    }

    @Override
    public void invalidate(final String experimentName, final Experiment.Builder builder) {
        final ExperimentEntity entity =
                ds.find(ExperimentEntity.class).filter(Filters.eq("name", experimentName)).first();
        if (entity == null) {
            return;
        }
        final Experiment experiment = entity.toExperiment(builder);
        if (experiment.isActive()) {
            cachedExperiments.put(experimentName, experiment);
        } else {
            cachedExperiments.remove(experimentName);
        }
    }

    @Override
    public void update(final Experiment experiment) {
        cachedExperiments.put(experiment.getName(), experiment);
    }

    @Override
    public void delete(final String experimentName) {
        cachedExperiments.remove(experimentName);
    }

    @Override
    public boolean checkIfAnyStale() {
        return revisionManager.checkIfAnyStale();
    }

    @Override
    public boolean checkIfStale(final String experimentName) {
        return revisionManager.checkIfStale(experimentName);
    }
}
