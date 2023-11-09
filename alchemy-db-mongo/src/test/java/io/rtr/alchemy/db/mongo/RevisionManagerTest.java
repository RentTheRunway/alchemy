package io.rtr.alchemy.db.mongo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.rtr.alchemy.db.mongo.models.ExperimentEntity;
import io.rtr.alchemy.db.mongo.models.MetadataEntity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.query.FieldEnd;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

public class RevisionManagerTest {
    private AdvancedDatastore ds;
    private UpdateOperations updateOperations;
    private long revision;

    @Before
    public void setUp() {
        revision = Long.MIN_VALUE;
        ds = mock(AdvancedDatastore.class);
        doAnswer(
                        new Answer() {
                            @Override
                            public Object answer(InvocationOnMock invocation) throws Throwable {
                                return MetadataEntity.of("revision", revision);
                            }
                        })
                .when(ds)
                .get(eq(MetadataEntity.class), anyString());

        doAnswer(
                        new Answer() {
                            @Override
                            public Object answer(InvocationOnMock invocation) throws Throwable {
                                return MetadataEntity.of("revision", ++revision);
                            }
                        })
                .when(ds)
                .findAndModify(any(Query.class), any(UpdateOperations.class));

        final Query query = mock(Query.class);
        final FieldEnd fieldEnd = mock(FieldEnd.class);
        updateOperations = mock(UpdateOperations.class);
        doReturn(fieldEnd).when(query).field(anyString());
        doReturn(query).when(fieldEnd).equal(any());

        doReturn(query).when(ds).createQuery(any(Class.class));
        doReturn(updateOperations).when(ds).createUpdateOperations(any(Class.class));
        doReturn(updateOperations).when(updateOperations).inc(anyString());
    }

    private void makeInsertSetRevision() {
        doAnswer(
                        new Answer() {
                            @Override
                            public Object answer(InvocationOnMock invocation) throws Throwable {
                                final MetadataEntity entity =
                                        (MetadataEntity) invocation.getArguments()[0];
                                revision = (Long) entity.value;
                                return null;
                            }
                        })
                .when(ds)
                .insert(any(Object.class));
    }

    @Test
    public void testInitializeNew() {
        makeInsertSetRevision();
        assertEquals(Long.MIN_VALUE + 1, new RevisionManager(ds).nextRevision());
    }

    @Test
    public void testInitializeExisting() {
        assertEquals(Long.MIN_VALUE + 1, new RevisionManager(ds).nextRevision());
    }

    @Test
    public void testNextRevision() {
        final RevisionManager revisionManager = new RevisionManager(ds);

        assertEquals(Long.MIN_VALUE + 1, revisionManager.nextRevision());
        assertEquals(Long.MIN_VALUE + 2, revisionManager.nextRevision());
        assertEquals(Long.MIN_VALUE + 3, revisionManager.nextRevision());
        verify(updateOperations, times(3)).inc(anyString());
    }

    @Test
    public void testCheckIfStale() {
        final RevisionManager revisionManager = new RevisionManager(ds);
        revisionManager.checkIfStale("foo");
        verify(ds).get(eq(ExperimentEntity.class), eq("foo"));
    }

    @Test
    public void testCheckIfAnyStale() {
        final RevisionManager revisionManager = new RevisionManager(ds);
        revisionManager.checkIfAnyStale();
        verify(ds).get(eq(MetadataEntity.class), anyString());
    }

    @Test
    public void testSetLatestRevision() {
        // our experiment will always be at revision Long.MIN_VALUE + 1
        doAnswer(
                        new Answer() {
                            @Override
                            public Object answer(InvocationOnMock invocation) throws Throwable {
                                final ExperimentEntity entity = mock(ExperimentEntity.class);
                                entity.revision = Long.MIN_VALUE + 1;
                                return entity;
                            }
                        })
                .when(ds)
                .get(eq(ExperimentEntity.class), anyString());

        final RevisionManager revisionManager = new RevisionManager(ds);

        revision = Long.MIN_VALUE; // our databases' revision
        revisionManager.setLatestRevision(Long.MIN_VALUE); // revision manager's internal revision
        assertFalse(revisionManager.checkIfAnyStale());
        assertTrue(revisionManager.checkIfStale("foo"));

        revision = Long.MIN_VALUE + 1; // our databases' revision
        revisionManager.setLatestRevision(Long.MIN_VALUE); // revision manager's internal revision
        assertTrue(revisionManager.checkIfAnyStale());

        revisionManager.setLatestRevision(
                Long.MIN_VALUE + 1); // revision manager's internal revision
        assertFalse(revisionManager.checkIfStale("foo"));
    }
}
