package com.rtr.alchemy.db.memory;

import com.rtr.alchemy.db.ExperimentsCache;
import com.rtr.alchemy.db.ExperimentsDatabaseProvider;
import com.rtr.alchemy.db.ExperimentsStore;

public class MemoryDatabaseProvider implements ExperimentsDatabaseProvider {
    private final MemoryDatabase db = new MemoryDatabase();

    @Override
    public ExperimentsCache createCache() {
        return new MemoryExperimentsCache(db);
    }

    @Override
    public ExperimentsStore createStore() {
        return new MemoryExperimentsStore(db);
    }
}
