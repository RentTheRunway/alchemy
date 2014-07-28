package io.rtr.alchemy.client.providers;

import io.rtr.alchemy.db.ExperimentsStoreProvider;
import io.rtr.alchemy.db.memory.MemoryStoreProvider;
import io.rtr.alchemy.service.config.StoreProviderConfiguration;

public class MemoryStoreConfiguration extends StoreProviderConfiguration {
    @Override
    public ExperimentsStoreProvider createProvider() {
        return new MemoryStoreProvider();
    }
}
