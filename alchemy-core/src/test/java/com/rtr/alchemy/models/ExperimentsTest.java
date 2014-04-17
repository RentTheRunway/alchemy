package com.rtr.alchemy.models;

import com.rtr.alchemy.db.ExperimentsCache;
import com.rtr.alchemy.db.ExperimentsDatabaseProvider;
import com.rtr.alchemy.db.ExperimentsStore;
import com.rtr.alchemy.db.Filter;
import com.rtr.alchemy.identities.Identity;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class ExperimentsTest {
    private ExperimentsStore store;
    private ExperimentsCache cache;
    private Experiments experiments;

    @Before
    public void setUp() {
        final ExperimentsDatabaseProvider provider = mock(ExperimentsDatabaseProvider.class);
        store = mock(ExperimentsStore.class);
        cache = mock(ExperimentsCache.class);
        doReturn(store).when(provider).createStore();
        doReturn(cache).when(provider).createCache();
        experiments = new Experiments(provider);
    }


    @Test
    public void testGetActiveTreatment() {
        final Identity identity = mock(Identity.class);
        doReturn(0L).when(identity).getHash(anyInt());
        experiments.getActiveTreatment("foo", identity);
        verifyZeroInteractions(store);
        verify(cache).getActiveExperiments();
    }

    @Test
    public void testGetActiveTreatments() {
        experiments.getActiveTreatments();
        verifyZeroInteractions(store);
        verify(cache).getActiveExperiments();
    }

    @Test
    public void testGetActiveExperiments() {
        experiments.getActiveExperiments();
        verifyZeroInteractions(store);
        verify(cache).getActiveExperiments();
    }

    @Test
    public void testCreate() {
        experiments.create("foo");
        verifyZeroInteractions(store);
        verifyZeroInteractions(cache);
    }

    @Test
    public void testSave() {
        final Experiment experiment = experiments.create("foo").save();
        verify(store).save(eq(experiment));
        verifyZeroInteractions(cache);
    }

    @Test
    public void testFind() {
        experiments.find();
        verify(store).find(eq(Filter.NONE));
        verifyZeroInteractions(cache);
    }

    @Test
    public void testFindFiltered() {
        final Filter filter = Filter.criteria().filter("foo").offset(1).limit(2).build();
        experiments.find(filter);
        verify(store).find(eq(filter));
        verifyZeroInteractions(cache);
    }

    @Test
    public void testDelete() {
        experiments.delete("foo");
        verify(store).delete(eq("foo"));
        verifyZeroInteractions(cache);
    }
}
