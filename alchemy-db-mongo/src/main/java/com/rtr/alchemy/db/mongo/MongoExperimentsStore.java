package com.rtr.alchemy.db.mongo;

import com.rtr.alchemy.db.ExperimentsStore;
import com.rtr.alchemy.db.Filter;
import com.rtr.alchemy.models.Experiment;

public class MongoExperimentsStore implements ExperimentsStore {
    @Override
    public void save(Experiment experiment) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Experiment load(String experimentName, Experiment.Builder builder) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void delete(String experimentName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<Experiment> find(Filter filter, Experiment.BuilderFactory factory) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
