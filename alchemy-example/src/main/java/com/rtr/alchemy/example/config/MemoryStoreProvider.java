package com.rtr.alchemy.example.config;

import com.rtr.alchemy.db.ExperimentsStoreProvider;
import com.rtr.alchemy.service.config.StoreProviderConfiguration;

/**
 * Configuration object for creating an in-memory provider
 */
public class MemoryStoreProvider extends StoreProviderConfiguration {
    @Override
    public ExperimentsStoreProvider createProvider() {
        return new com.rtr.alchemy.db.memory.MemoryStoreProvider();
    }
}
