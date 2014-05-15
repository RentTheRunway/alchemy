package com.rtr.alchemy.example.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rtr.alchemy.db.ExperimentsStoreProvider;
import com.rtr.alchemy.service.config.StoreProviderConfiguration;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Configuration object for creating a MongoDB provider with given parameters
 */
public class MongoStoreProvider extends StoreProviderConfiguration {
    @NotNull
    private final List<String> hosts;

    @NotNull
    private final String db;

    @JsonCreator
    public MongoStoreProvider(@JsonProperty("hosts") List<String> hosts,
                              @JsonProperty("db") String db) {
        this.hosts = hosts;
        this.db = db;
    }

    public List<String> getHosts() {
        return hosts;
    }

    public String getDb() {
        return db;
    }

    @Override
    public ExperimentsStoreProvider createProvider() {
        return new com.rtr.alchemy.db.mongo.MongoStoreProvider(hosts, db);
    }
}