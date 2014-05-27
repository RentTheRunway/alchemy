package com.rtr.alchemy.db.mongo;

import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.rtr.alchemy.db.ExperimentsStoreProvider;
import com.rtr.alchemy.db.ExperimentsCache;
import com.rtr.alchemy.db.ExperimentsStore;
import com.rtr.alchemy.db.mongo.util.DateTimeConverter;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.Morphia;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * A provider for MongoDB which implements the store and cache for using MongoDB as a backend
 */
public class MongoStoreProvider implements ExperimentsStoreProvider {
    private final MongoClient client;
    private final ExperimentsStore store;
    private final ExperimentsCache cache;

    public static Builder newBuilder() {
        return new Builder();
    }

    private MongoStoreProvider(List<ServerAddress> hosts,
                               List<MongoCredential> credentials,
                               MongoClientOptions options,
                               String database) {

        final Morphia morphia = new Morphia();
        morphia.getMapper().getConverters().addConverter(DateTimeConverter.class);
        client = options == null ? new MongoClient(hosts, credentials) : new MongoClient(hosts, credentials, options);

        final AdvancedDatastore ds = (AdvancedDatastore) morphia.createDatastore(client, database);
        final RevisionManager revisionManager = new RevisionManager(ds);
        this.store = new MongoExperimentsStore(ds, revisionManager);
        this.cache = new MongoExperimentsCache(ds, revisionManager);
    }

    @Override
    public ExperimentsStore getStore() {
        return store;
    }

    @Override
    public ExperimentsCache getCache() {
        return cache;
    }

    @Override
    public void close() throws IOException {
        client.close();
    }

    public static class Builder {
        private List<ServerAddress> hosts;
        private List<MongoCredential> credentials;
        private MongoClientOptions options;
        private String database;

        public Builder() {
            this.hosts = Lists.newArrayList();
            this.credentials = Lists.newArrayList();
            this.database = "experiments";
        }

        public Builder setDatabase(String database) {
            this.database = database;
            return this;
        }

        public Builder setOptions(MongoClientOptions options) {
            this.options = options;
            return this;
        }

        public Builder addCredential(MongoCredential credential) {
            this.credentials.add(credential);
            return this;
        }

        public Builder addHost(ServerAddress host) {
            hosts.add(host);
            return this;
        }

        public MongoStoreProvider build() throws UnknownHostException {
            if (hosts.isEmpty()) {
                hosts.add(new ServerAddress(ServerAddress.defaultHost(), ServerAddress.defaultPort()));
            }

            return new MongoStoreProvider(
                hosts,
                credentials,
                options,
                database
            );
        }
    }
}
