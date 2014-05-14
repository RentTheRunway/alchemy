package com.rtr.alchemy.db.mongo;

import com.mongodb.MongoException;
import com.rtr.alchemy.db.mongo.models.ExperimentEntity;
import com.rtr.alchemy.db.mongo.models.MetadataEntity;
import org.mongodb.morphia.AdvancedDatastore;

/**
 * Manages what revision experiments are at in order to check when experiments are stale
 */
public class RevisionManager {
    private static final String NAME = "revision";
    private final AdvancedDatastore ds;
    private volatile Long latestRevision;

    public RevisionManager(AdvancedDatastore ds) {
        this.ds = ds;
        initializeRevision();
    }

    private Long getValue() {
        final MetadataEntity entity = ds.get(MetadataEntity.class, NAME);

        if (entity == null) {
            return null;
        }

        return (Long) entity.value;
    }

    private Long initialize() {
        try {
            ds.insert(MetadataEntity.of(NAME, Long.MIN_VALUE));
            return Long.MIN_VALUE;
        } catch (final MongoException.DuplicateKey e) {
            return getValue();
        }
    }

    private void initializeRevision() {
        latestRevision = initialize();
    }

    private Long getExperimentRevision(String experimentName) {
        final ExperimentEntity experiment = ds.get(ExperimentEntity.class, experimentName);
        return experiment != null ? experiment.revision : null;
    }

    public long nextRevision() {
        return
            (Long) ds
                .findAndModify(
                    ds.createQuery(MetadataEntity.class).field("name").equal(NAME),
                    ds.createUpdateOperations(MetadataEntity.class).inc("value")
                ).value;
    }

    public void setLatestRevision(Long revision) {
        latestRevision = revision;
    }

    public boolean checkIfAnyStale() {
        final Long revision = getValue();
        return revision != null && revision > latestRevision;
    }

    public boolean checkIfStale(String experimentName) {
        final Long revision = getExperimentRevision(experimentName);
        return revision != null && revision > latestRevision;
    }
}