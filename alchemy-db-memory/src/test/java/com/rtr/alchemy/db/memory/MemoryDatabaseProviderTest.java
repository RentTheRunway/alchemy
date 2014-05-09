package com.rtr.alchemy.db.memory;


import com.rtr.alchemy.db.ExperimentsStoreProvider;
import com.rtr.alchemy.testing.db.ExperimentsDatabaseProviderTest;

public class MemoryDatabaseProviderTest extends ExperimentsDatabaseProviderTest {
    @Override
    protected ExperimentsStoreProvider createProvider() {
        return new MemoryStoreProvider();
    }
}
