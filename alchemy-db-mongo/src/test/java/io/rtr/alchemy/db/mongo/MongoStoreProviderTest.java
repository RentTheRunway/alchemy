package io.rtr.alchemy.db.mongo;

import static org.junit.Assert.fail;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;

import io.rtr.alchemy.db.ExperimentsStoreProvider;
import io.rtr.alchemy.testing.db.ExperimentsStoreProviderTest;

import org.junit.Ignore;

@Ignore("must be run manually and requires a local Mongo instance")
public class MongoStoreProviderTest extends ExperimentsStoreProviderTest {
    private static final String DATABASE_NAME = "test_experiments";

    @Override
    protected ExperimentsStoreProvider createProvider() {
        return MongoStoreProvider.newBuilder().setDatabase(DATABASE_NAME).build();
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
