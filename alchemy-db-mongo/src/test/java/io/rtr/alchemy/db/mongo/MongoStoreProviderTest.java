package io.rtr.alchemy.db.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import io.rtr.alchemy.db.ExperimentsStoreProvider;
import io.rtr.alchemy.testing.db.ExperimentsStoreProviderTest;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.Assert.fail;

@Testcontainers
public class MongoStoreProviderTest extends ExperimentsStoreProviderTest {
    private static final String DATABASE_NAME = "test_experiments";

    @Container static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.14");

    @Override
    protected ExperimentsStoreProvider createProvider() {
        final String connStr = mongoDBContainer.getReplicaSetUrl(DATABASE_NAME);
        final MongoClientURI uri = new MongoClientURI(connStr, MongoClientOptions.builder());
        MongoCredential credential =
                MongoCredential.createCredential(
                        uri.getUsername(), DATABASE_NAME, uri.getPassword());
        return MongoStoreProvider.newBuilder()
                .addCredential(credential)
                .setDatabase(DATABASE_NAME)
                .build();
    }

    @Override
    protected void resetStore() {
        MongoClient client = null;
        try {
            client = new MongoClient();
            client.dropDatabase(DATABASE_NAME);
        } catch (MongoException e) {
            fail("could not delete database");
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
