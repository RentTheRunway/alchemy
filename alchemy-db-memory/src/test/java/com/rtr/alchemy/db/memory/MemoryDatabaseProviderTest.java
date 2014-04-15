package com.rtr.alchemy.db.memory;


import com.rtr.alchemy.db.ExperimentsDatabaseProvider;
import com.rtr.alchemy.testing.db.ExperimentsDatabaseProviderTest;

public class MemoryDatabaseProviderTest extends ExperimentsDatabaseProviderTest {
    @Override
    protected ExperimentsDatabaseProvider createProvider() {
        return new MemoryDatabaseProvider();
    }
}
