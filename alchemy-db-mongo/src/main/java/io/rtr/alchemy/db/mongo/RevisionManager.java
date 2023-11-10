package io.rtr.alchemy.db.mongo;

import com.mongodb.DuplicateKeyException;
import com.mongodb.client.model.ReturnDocument;

import dev.morphia.Datastore;
import dev.morphia.ModifyOptions;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperators;

import io.rtr.alchemy.db.mongo.models.ExperimentEntity;
import io.rtr.alchemy.db.mongo.models.MetadataEntity;

import java.util.Optional;

/** Manages what revision experiments are at in order to check when experiments are stale */
public class RevisionManager {
    private static final String NAME = "revision";
    private final Datastore ds;
    private volatile Long latestRevision;

    public RevisionManager(final Datastore ds) {
        this.ds = ds;
        initializeRevision();
    }

    private Long getValue() {
        final MetadataEntity entity =
                ds.find(MetadataEntity.class).filter(Filters.eq("name", NAME)).first();

        if (entity == null) {
            return null;
        }

        return (Long) entity.value;
    }

    private Long initialize() {
        try {
            ds.insert(MetadataEntity.of(NAME, Long.MIN_VALUE));
            return Long.MIN_VALUE;
        } catch (final DuplicateKeyException e) {
            return getValue();
        }
    }

    private void initializeRevision() {
        latestRevision = initialize();
    }

    private Long getExperimentRevision(final String experimentName) {
        final ExperimentEntity experiment =
                ds.find(ExperimentEntity.class).filter(Filters.eq("name", experimentName)).first();
        return experiment != null ? experiment.revision : null;
    }

    public long nextRevision() {
        final MetadataEntity incremented =
                ds.find(MetadataEntity.class)
                        .filter(Filters.eq("name", NAME))
                        .modify(
                                new ModifyOptions().returnDocument(ReturnDocument.AFTER),
                                UpdateOperators.inc("value"));

        return Optional.ofNullable(incremented).map(i -> (Long) i.value).orElse(1L);
    }

    public void setLatestRevision(final Long revision) {
        latestRevision = revision;
    }

    public boolean checkIfAnyStale() {
        final Long revision = getValue();
        return revision != null && revision > latestRevision;
    }

    public boolean checkIfStale(final String experimentName) {
        final Long revision = getExperimentRevision(experimentName);
        return revision != null && revision > latestRevision;
    }
}
