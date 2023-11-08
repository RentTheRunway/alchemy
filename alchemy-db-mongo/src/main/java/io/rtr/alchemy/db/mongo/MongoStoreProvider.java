package io.rtr.alchemy.db.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import dev.morphia.AdvancedDatastore;
import dev.morphia.Morphia;

import io.rtr.alchemy.db.ExperimentsCache;
import io.rtr.alchemy.db.ExperimentsStore;
import io.rtr.alchemy.db.ExperimentsStoreProvider;
import io.rtr.alchemy.db.mongo.util.DateTimeConverter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/** A provider for MongoDB which implements the store and cache for using MongoDB as a backend */
public final class MongoStoreProvider implements ExperimentsStoreProvider {
    private final MongoClient client;
    private final ExperimentsStore store;
    private final ExperimentsCache cache;

    public static Builder newBuilder() {
        return new Builder();
    }

    private MongoStoreProvider(final MongoClient client, final String database) {
        final Morphia morphia = new Morphia();
        morphia.getMapper().getOptions().setStoreEmpties(true);
        morphia.getMapper().getConverters().addConverter(DateTimeConverter.class);

        this.client = client;
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
    public void close() {
        client.close();
    }

    public static class Builder {
        private final List<ServerAddress> hosts = new ArrayList<>();
        private String database = "experiments";
        private MongoClientOptions options = MongoClientOptions.builder().build();
        @Nullable private MongoClientURI uri;
        @Nullable private MongoCredential credential;

        public Builder addHost(final ServerAddress host) {
            hosts.add(host);
            return this;
        }

        public Builder setDatabase(final String database) {
            this.database = database;
            return this;
        }

        public Builder setOptions(final MongoClientOptions options) {
            this.options = options;
            return this;
        }

        /** Note that all other fields (besides {@code database}) will be ignored if this is set! */
        public Builder setUri(final MongoClientURI uri) {
            this.uri = uri;
            return this;
        }

        public Builder setCredential(final MongoCredential credential) {
            this.credential = credential;
            return this;
        }

        public MongoStoreProvider build() {
            if (hosts.isEmpty() && uri == null) {
                hosts.add(
                        new ServerAddress(
                                ServerAddress.defaultHost(), ServerAddress.defaultPort()));
            }

            if (uri != null) {
                return new MongoStoreProvider(new MongoClient(uri), database);
            } else {
                return new MongoStoreProvider(
                        new MongoClient(hosts, credential, options), database);
            }
        }
    }
}
