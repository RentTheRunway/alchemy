package com.rtr.alchemy.db.memory;

import com.google.common.collect.Maps;
import com.rtr.alchemy.models.Experiment;

import java.util.Map;

public class MemoryDatabase {
    private final Map<String, Experiment> experiments = Maps.newHashMap();
    private final Object lock = new Object();

    public Map<String, Experiment> getExperiments() {
        return experiments;
    }

    public Object lock() {
        return lock;
    }
}
