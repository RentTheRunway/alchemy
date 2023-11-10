package io.rtr.alchemy.db.mongo;

import static io.rtr.alchemy.db.mongo.util.MongoDbTestHelper.MONGODB_DATABASE;
import static io.rtr.alchemy.db.mongo.util.MongoDbTestHelper.MONGODB_IMAGE;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

import io.rtr.alchemy.db.ExperimentsStoreProvider;
import io.rtr.alchemy.testing.db.ExperimentsStoreProviderTest;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.stream.Collectors;

@Testcontainers
public class MongoStoreProviderTest extends ExperimentsStoreProviderTest {
    @Container static MongoDBContainer mongoDBContainer = new MongoDBContainer(MONGODB_IMAGE);

    private static MongoClientURI uri;

    @BeforeAll
    static void setUpClass() {
        uri = new MongoClientURI(mongoDBContainer.getReplicaSetUrl(MONGODB_DATABASE));
    }

    @Override
    protected ExperimentsStoreProvider createProvider() {
        return MongoStoreProvider.newBuilder()
                .setHosts(
                        uri.getHosts().stream()
                                .map(ServerAddress::new)
                                .collect(Collectors.toList()))
                .build();
    }

    @Override
    protected void resetStore() {
        try (final MongoClient client = new MongoClient(uri)) {
            client.dropDatabase(MONGODB_DATABASE);
        } catch (final MongoException e) {
            throw new IllegalStateException(e);
        }
    }
}
