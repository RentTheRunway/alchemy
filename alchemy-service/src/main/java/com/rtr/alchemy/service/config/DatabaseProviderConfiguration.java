package com.rtr.alchemy.service.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.rtr.alchemy.db.ExperimentsDatabaseProvider;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
public abstract class DatabaseProviderConfiguration {
    public abstract ExperimentsDatabaseProvider createProvider();
}
