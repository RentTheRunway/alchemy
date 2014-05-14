package com.rtr.alchemy.models;

import com.google.common.collect.ImmutableMap;
import com.rtr.alchemy.caching.CacheStrategy;
import com.rtr.alchemy.db.ExperimentsCache;
import com.rtr.alchemy.db.ExperimentsStoreProvider;
import com.rtr.alchemy.db.ExperimentsStore;
import com.rtr.alchemy.db.Filter;
import com.rtr.alchemy.identities.Identity;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class ExperimentsTest {
    private ExperimentsStore store;
    private ExperimentsCache cache;
    private Experiments experiments;

    @Before
    public void setUp() {
        final ExperimentsStoreProvider provider = mock(ExperimentsStoreProvider.class);
        store = mock(ExperimentsStore.class);
        cache = mock(ExperimentsCache.class);
        doReturn(store).when(provider).getStore();
        doReturn(cache).when(provider).getCache();
        experiments =
            Experiments
                .using(provider)
                .using(mock(CacheStrategy.class))
                .build();

        // suppress initial invalidateAll() call
        reset(cache);
    }

    @Test
    public void testGetActiveTreatment() {
        final Identity identity = mock(Identity.class);
        final Experiment experiment = mock(Experiment.class);
        doReturn(ImmutableMap.of("foo", experiment))
            .when(cache)
            .getActiveExperiments();
        experiments.getActiveTreatment("foo", identity);
        verifyZeroInteractions(store);
        verify(cache).getActiveExperiments();
        verify(experiment).getOverride(eq(identity));
    }

    @Test
    public void testGetActiveTreatments() {
        final Experiment experiment = mock(Experiment.class);
        final Identity identity = mock(Identity.class);
        doReturn(ImmutableMap.of("foo", experiment))
            .when(cache)
            .getActiveExperiments();
        experiments.getActiveTreatments(identity);
        verifyZeroInteractions(store);
        verify(cache).getActiveExperiments();
        verify(experiment).getOverride(eq(identity));
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
        verify(store).find(eq(Filter.NONE), any(Experiment.BuilderFactory.class));
        verifyZeroInteractions(cache);
    }

    @Test
    public void testFindFiltered() {
        final Filter filter = Filter.criteria().filter("foo").offset(1).limit(2).build();
        experiments.find(filter);
        verify(store).find(eq(filter), any(Experiment.BuilderFactory.class));
        verifyZeroInteractions(cache);
    }

    @Test
    public void testDelete() {
        experiments.delete("foo");
        verify(store).delete(eq("foo"));
        verifyZeroInteractions(cache);
    }
}
