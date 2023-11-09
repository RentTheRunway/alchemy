package io.rtr.alchemy.example.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.net.HostAndPort;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import io.rtr.alchemy.db.ExperimentsStoreProvider;
import io.rtr.alchemy.service.config.StoreProviderConfiguration;

import java.net.UnknownHostException;
import java.util.List;

import javax.validation.constraints.NotNull;

/** Configuration object for creating a MongoDB provider with given parameters */
public class MongoStoreProvider extends StoreProviderConfiguration {
    @NotNull private final List<HostAndPort> hosts;

    @NotNull private final String db;

    private final String username;

    private final String password;

    @JsonCreator
    public MongoStoreProvider(
            @JsonProperty("hosts") List<HostAndPort> hosts,
            @JsonProperty("db") String db,
            @JsonProperty("username") String username,
            @JsonProperty("password") String password) {
        this.hosts = hosts;
        this.db = db;
        this.username = username;
        this.password = password;
    }

    public List<HostAndPort> getHosts() {
        return hosts;
    }

    public String getDb() {
        return db;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public ExperimentsStoreProvider createProvider() throws UnknownHostException {
        final io.rtr.alchemy.db.mongo.MongoStoreProvider.Builder builder =
                io.rtr.alchemy.db.mongo.MongoStoreProvider.newBuilder();
        for (final HostAndPort host : hosts) {
            if (!host.hasPort()) {
                builder.addHost(new ServerAddress(host.getHost()));
            } else {
                builder.addHost(new ServerAddress(host.getHost(), host.getPort()));
            }
        }

        if (username != null) {
            builder.addCredential(
                    MongoCredential.createPlainCredential(username, db, password.toCharArray()));
        }

        builder.setDatabase(db);

        return builder.build();
    }
}
