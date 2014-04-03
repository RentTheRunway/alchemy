package com.rtr.alchemy.core;

import com.google.inject.Inject;
import com.rtr.alchemy.db.ExperimentsCache;
import com.rtr.alchemy.db.ExperimentsStore;

/**
 * The main class for accessing experiments
 */
public class Experiments {
    private final ExperimentsStore store;
    private final ExperimentsCache cache;

    @Inject
    public Experiments(ExperimentsStore store, ExperimentsCache cache) {
        this.store = store;
        this.cache = cache;
    }
}
