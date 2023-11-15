package io.rtr.alchemy.db.mongo;

import static io.rtr.alchemy.db.mongo.util.MongoDbTestHelper.MONGODB_DATABASE;
import static io.rtr.alchemy.db.mongo.util.MongoDbTestHelper.MONGODB_IMAGE;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;

import dev.morphia.Datastore;
import dev.morphia.Morphia;

import io.rtr.alchemy.db.mongo.models.MetadataEntity;
import io.rtr.alchemy.db.mongo.util.DateTimeCodec;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class RevisionManagerTest {

    @Container static MongoDBContainer mongoDBContainer = new MongoDBContainer(MONGODB_IMAGE);

    private static Datastore ds;
    private RevisionManager revisionManager;

    @BeforeAll
    static void setUpClass() {
        final MongoClientSettings mongoClientSettings =
                MongoClientSettings.builder()
                        .applyConnectionString(
                                new ConnectionString(
                                        mongoDBContainer.getReplicaSetUrl(MONGODB_DATABASE)))
                        .codecRegistry(
                                CodecRegistries.fromRegistries(
                                        CodecRegistries.fromCodecs(new DateTimeCodec()),
                                        MongoClientSettings.getDefaultCodecRegistry()))
                        .build();

        ds = Morphia.createDatastore(MongoClients.create(mongoClientSettings));
    }

    @BeforeEach
    void setUp() {
        revisionManager = new RevisionManager(ds);
    }

    @AfterEach
    void tearDown() {
        ds.getCollection(MetadataEntity.class).deleteMany(new Document());
    }

    @Test
    void testInitializeNew() {
        final MetadataEntity metadata = ds.find(MetadataEntity.class).first();

        assertNotNull(metadata);
        assertEquals(Long.MIN_VALUE, metadata.value);
    }

    @Test
    void testInitializeExisting() {
        revisionManager.nextRevision();

        final MetadataEntity metadata = ds.find(MetadataEntity.class).first();

        assertNotNull(metadata);
        assertEquals(Long.MIN_VALUE + 1, metadata.value);
    }

    @Test
    void testNextRevision() {
        assertEquals(Long.MIN_VALUE + 1, revisionManager.nextRevision());
        assertEquals(Long.MIN_VALUE + 2, revisionManager.nextRevision());
        assertEquals(Long.MIN_VALUE + 3, revisionManager.nextRevision());
    }

    @Test
    void testCheckIfStale() {
        assertFalse(revisionManager.checkIfStale("foo"));
    }

    @Test
    void testCheckIfAnyStale() {
        assertFalse(revisionManager.checkIfAnyStale());

        revisionManager.nextRevision();

        assertTrue(revisionManager.checkIfAnyStale());
    }

    @Test
    void testSetLatestRevision() {
        revisionManager.setLatestRevision(Long.MIN_VALUE + 1);
        assertFalse(revisionManager.checkIfAnyStale());
    }
}
