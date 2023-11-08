package io.rtr.alchemy.db.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;

import io.rtr.alchemy.db.ExperimentsStoreProvider;
import io.rtr.alchemy.testing.db.ExperimentsStoreProviderTest;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class MongoStoreProviderTest extends ExperimentsStoreProviderTest {
    private static final String DATABASE_NAME = "test_experiments";

    @Container static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.25");

    private static MongoClientURI uri;

    @BeforeAll
    static void setUpClass() {
        final String connStr = mongoDBContainer.getReplicaSetUrl(DATABASE_NAME);
        uri = new MongoClientURI(connStr, MongoClientOptions.builder());
    }

    @Override
    protected ExperimentsStoreProvider createProvider() {
        return MongoStoreProvider.newBuilder().setUri(uri).setDatabase(DATABASE_NAME).build();
    }

    @Override
    protected void resetStore() {
        try (final MongoClient client = new MongoClient(uri)) {
            client.dropDatabase(DATABASE_NAME);
        } catch (final MongoException e) {
            throw new IllegalStateException(e);
        }
    }
}
