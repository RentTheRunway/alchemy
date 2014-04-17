package com.rtr.alchemy.db;

/**
 * An interface for implementing a provider that is configurable
 */
public interface ExperimentsDatabaseProvider {
    ExperimentsCache createCache();
    ExperimentsStore createStore();
}
