package io.rtr.alchemy.db.memory;


import io.rtr.alchemy.db.ExperimentsStoreProvider;
import io.rtr.alchemy.testing.db.ExperimentsStoreProviderTest;

public class MemoryStoreProviderTest extends ExperimentsStoreProviderTest {
    @Override
    protected ExperimentsStoreProvider createProvider() {
        return new MemoryStoreProvider();
    }

    @Override
    protected void resetStore() {
        // nothing has to be done because each instance of MemoryStoreProvider is a new store
    }
}
