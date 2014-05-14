package com.rtr.alchemy.db.mongo;

import com.google.common.collect.Maps;
import com.rtr.alchemy.db.ExperimentsCache;
import com.rtr.alchemy.db.mongo.models.ExperimentEntity;
import com.rtr.alchemy.models.Experiment;
import org.mongodb.morphia.Datastore;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * A cache backed by MongoDB which allows for quick cached access to Experiments
 */
public class MongoExperimentsCache implements ExperimentsCache {
    private final RevisionManager revisionManager;
    private final Datastore ds;
    private volatile Map<String, Experiment> cachedExperiments;

    public MongoExperimentsCache(Datastore ds, RevisionManager revisionManager) {
        this.ds = ds;
        this.revisionManager = revisionManager;
    }

    @Override
    public void invalidateAll(Experiment.BuilderFactory factory) {
        final Iterator<ExperimentEntity> iterator =
            ds
                .find(ExperimentEntity.class)
                .field("active").equal(true)
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
    public void invalidate(String experimentName, Experiment.Builder builder) {
        final ExperimentEntity entity = ds.get(ExperimentEntity.class, experimentName);
        final Experiment experiment = entity.toExperiment(builder);
        if (experiment.isActive()) {
            cachedExperiments.put(experimentName, experiment);
        } else {
            cachedExperiments.remove(experimentName);
        }

    }

    @Override
    public void update(Experiment experiment) {
        cachedExperiments.put(experiment.getName(), experiment);
    }

    @Override
    public void delete(String experimentName) {
        cachedExperiments.remove(experimentName);
    }

    @Override
    public boolean checkIfAnyStale() {
        return revisionManager.checkIfAnyStale();
    }

    @Override
    public boolean checkIfStale(String experimentName) {
        return revisionManager.checkIfStale(experimentName);
    }
}
