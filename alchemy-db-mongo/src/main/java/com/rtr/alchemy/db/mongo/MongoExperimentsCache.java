package com.rtr.alchemy.db.mongo;

import com.rtr.alchemy.db.ExperimentsCache;
import com.rtr.alchemy.models.Experiment;

import java.util.Map;

public class MongoExperimentsCache implements ExperimentsCache {
    @Override
    public void invalidateAll(Experiment.BuilderFactory factory) {
    }

    @Override
    public Map<String, Experiment> getActiveExperiments() {
        return null;
    }

    @Override
    public void invalidate(String experimentName, Experiment.Builder builder) {

    }

    @Override
    public void update(Experiment experiment) {

    }

    @Override
    public void delete(String experimentName) {

    }

    @Override
    public boolean checkIfAnyStale() {
        return false;
    }

    @Override
    public boolean checkIfStale(String experimentName) {
        return false;
    }
}
