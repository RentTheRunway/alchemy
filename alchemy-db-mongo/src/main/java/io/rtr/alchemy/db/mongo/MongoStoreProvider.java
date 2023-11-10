package io.rtr.alchemy.db.mongo;

import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ClusterConnectionMode;

import dev.morphia.Datastore;
import dev.morphia.Morphia;

import io.rtr.alchemy.db.ExperimentsCache;
import io.rtr.alchemy.db.ExperimentsStore;
import io.rtr.alchemy.db.ExperimentsStoreProvider;
import io.rtr.alchemy.db.mongo.util.DateTimeCodec;

import org.bson.codecs.configuration.CodecRegistries;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/** A provider for MongoDB which implements the store and cache for using MongoDB as a backend */
public class MongoStoreProvider implements ExperimentsStoreProvider {
    private final MongoClient client;
    private final ExperimentsStore store;
    private final ExperimentsCache cache;

    private MongoStoreProvider(final MongoClient client) {
        this.client = client;
        final Datastore ds = Morphia.createDatastore(client);
        final RevisionManager revisionManager = new RevisionManager(ds);
        this.store = new MongoExperimentsStore(ds, revisionManager);
        this.cache = new MongoExperimentsCache(ds, revisionManager);
    }

    public static Builder newBuilder() {
        return new Builder();
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
        private List<ServerAddress> hosts = new ArrayList<>();
        @Nullable private MongoCredential credential;
        private MongoClientOptions options = MongoClientOptions.builder().build();
        private ClusterConnectionMode connectionMode = ClusterConnectionMode.SINGLE;

        @Deprecated
        public Builder setDatabase(final String database) {
            // No-op: This is set through the properties file
            return this;
        }

        public Builder setOptions(final MongoClientOptions options) {
            this.options = options;
            return this;
        }

        public Builder setCredential(final MongoCredential credential) {
            this.credential = credential;
            return this;
        }

        public Builder addHost(final ServerAddress host) {
            hosts.add(host);
            return this;
        }

        public Builder setHosts(final List<ServerAddress> hosts) {
            this.hosts = hosts;
            return this;
        }

        public Builder setClusterConnectionMode(final ClusterConnectionMode connectionMode) {
            this.connectionMode = connectionMode;
            return this;
        }

        public MongoStoreProvider build() {
            if (hosts.isEmpty()) {
                hosts.add(
                        new ServerAddress(
                                ServerAddress.defaultHost(), ServerAddress.defaultPort()));
            }

            final MongoClientSettings settings =
                    MongoClientOptions.builder(options)
                            .codecRegistry(
                                    CodecRegistries.fromRegistries(
                                            CodecRegistries.fromCodecs(new DateTimeCodec()),
                                            MongoClientSettings.getDefaultCodecRegistry()))
                            .build()
                            .asMongoClientSettings(hosts, null, connectionMode, credential);

            return new MongoStoreProvider(MongoClients.create(settings));
        }
    }
}
