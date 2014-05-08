package com.rtr.alchemy.db;

import java.io.Closeable;

/**
 * An interface for implementing a provider that is configurable
 */
public interface ExperimentsStoreProvider extends Closeable {
    ExperimentsCache getCache();
    ExperimentsStore getStore();
}
