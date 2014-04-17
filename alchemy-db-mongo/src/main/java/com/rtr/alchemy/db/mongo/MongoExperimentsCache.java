package com.rtr.alchemy.db.mongo;

import com.rtr.alchemy.db.ExperimentsCache;
import com.rtr.alchemy.models.Experiment;

import java.util.Map;

public class MongoExperimentsCache implements ExperimentsCache {
    @Override
    public void close() {
    }

    @Override
    public void invalidate() {
    }

    @Override
    public Map<String, Experiment> getActiveExperiments() {
        return null;
    }
}
