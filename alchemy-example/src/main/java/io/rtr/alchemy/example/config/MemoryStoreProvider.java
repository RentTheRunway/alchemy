package io.rtr.alchemy.example.config;

import io.rtr.alchemy.db.ExperimentsStoreProvider;
import io.rtr.alchemy.service.config.StoreProviderConfiguration;

/** Configuration object for creating an in-memory provider */
public class MemoryStoreProvider extends StoreProviderConfiguration {
    @Override
    public ExperimentsStoreProvider createProvider() {
        return new io.rtr.alchemy.db.memory.MemoryStoreProvider();
    }
}
