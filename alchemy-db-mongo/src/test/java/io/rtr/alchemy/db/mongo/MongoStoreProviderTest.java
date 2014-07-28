package io.rtr.alchemy.db.mongo;

import com.mongodb.MongoClient;
import io.rtr.alchemy.db.ExperimentsStoreProvider;
import io.rtr.alchemy.testing.db.ExperimentsStoreProviderTest;
import org.junit.Ignore;

import java.net.UnknownHostException;

import static org.junit.Assert.fail;

@Ignore("must be run manually and requires a local Mongo instance")
public class MongoStoreProviderTest extends ExperimentsStoreProviderTest {
    private static final String DATABASE_NAME = "test_experiments";

    @Override
    protected ExperimentsStoreProvider createProvider() throws Exception {
        return MongoStoreProvider
            .newBuilder()
            .setDatabase(DATABASE_NAME)
            .build();
    }

    @Override
    protected void resetStore() {
        MongoClient client = null;
        try {
            client = new MongoClient();
            client.dropDatabase(DATABASE_NAME);
        } catch (final UnknownHostException e) {
            fail("could not delete database");
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
