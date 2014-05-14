package com.rtr.alchemy.db.mongo;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.rtr.alchemy.db.ExperimentsStoreProvider;
import com.rtr.alchemy.db.ExperimentsCache;
import com.rtr.alchemy.db.ExperimentsStore;
import com.rtr.alchemy.db.mongo.util.DateTimeConverter;
import org.apache.commons.math3.util.Pair;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.Morphia;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * A provider for MongoDB which implements the store and cache for using MongoDB as a backend
 */
public class MongoStoreProvider implements ExperimentsStoreProvider {
    private final MongoClient client;
    private final ExperimentsStore store;
    private final ExperimentsCache cache;

    public MongoStoreProvider(String db) {
        this(ServerAddress.defaultHost(), db);
    }

    public MongoStoreProvider(String host, String db) {
        this(Lists.newArrayList(host), db);
    }

    public MongoStoreProvider(Iterable<String> hosts, String db) {
        final List<ServerAddress> addresses = parseAddresses(Lists.newArrayList(hosts));
        final Morphia morphia = new Morphia();

        morphia.getMapper().getConverters().addConverter(DateTimeConverter.class);
        client = new MongoClient(addresses);

        final AdvancedDatastore ds = (AdvancedDatastore) morphia.createDatastore(client, db);
        final RevisionManager revisionManager = new RevisionManager(ds);
        this.store = new MongoExperimentsStore(ds, revisionManager);
        this.cache = new MongoExperimentsCache(ds, revisionManager);
    }

    private static Pair<String, Integer> parseHostAndPort(String host) {
        try {
            final URI uri = new URI(String.format("my://%s", host));
            return
                uri.getPort() == -1 ?
                    new Pair<>(uri.getHost(), ServerAddress.defaultPort()) :
                    new Pair<>(uri.getHost(), uri.getPort());
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<ServerAddress> parseAddresses(List<String> hosts) {
        return Lists.transform(
            hosts,
            new Function<String, ServerAddress>() {
                @Override
                public ServerAddress apply(String input) {
                    final Pair<String, Integer> hostPort = parseHostAndPort(input);
                    try {
                        return new ServerAddress(hostPort.getFirst(), hostPort.getSecond());
                    } catch (final UnknownHostException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        );
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
}
