package io.rtr.alchemy.db.memory;

import com.google.common.collect.Maps;
import io.rtr.alchemy.db.ExperimentsCache;
import io.rtr.alchemy.db.ExperimentsStoreProvider;
import io.rtr.alchemy.db.ExperimentsStore;
import io.rtr.alchemy.models.Experiment;

import java.io.IOException;
import java.util.Map;

/**
 * A store provider storing and caching experiments in memory
 */
public class MemoryStoreProvider implements ExperimentsStoreProvider {
    private final Map<String, Experiment> db = Maps.newConcurrentMap();

    @Override
    public ExperimentsCache getCache() {
        return new MemoryExperimentsCache(db);
    }

    @Override
    public ExperimentsStore getStore() {
        return new MemoryExperimentsStore(db);
    }

    public void resetDatabase() {
        db.clear();
    }

    @Override
    public void close() throws IOException {
        db.clear();
    }
}
