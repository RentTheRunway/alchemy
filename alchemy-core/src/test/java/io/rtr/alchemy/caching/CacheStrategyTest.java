package io.rtr.alchemy.caching;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import io.rtr.alchemy.db.ExperimentsCache;
import io.rtr.alchemy.db.ExperimentsStore;
import io.rtr.alchemy.db.ExperimentsStoreProvider;
import io.rtr.alchemy.db.Filter;
import io.rtr.alchemy.filtering.FilterExpression;
import io.rtr.alchemy.identities.AttributesMap;
import io.rtr.alchemy.identities.Identity;
import io.rtr.alchemy.models.Experiment;
import io.rtr.alchemy.models.Experiments;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CacheStrategyTest {
    private CacheStrategy strategy;
    private ExperimentsCache cache;
    private Experiments experiments;
    private Experiment experiment1;
    private Experiment experiment2;

    @Before
    public void setUp() {
        final ExperimentsStoreProvider provider = mock(ExperimentsStoreProvider.class);
        final ExperimentsStore store = mock(ExperimentsStore.class);
        strategy = mock(CacheStrategy.class);
        cache = mock(ExperimentsCache.class);
        doReturn(store).when(provider).getStore();
        doReturn(cache).when(provider).getCache();

        experiment1 = mock(Experiment.class);
        doReturn("foo").when(experiment1).getName();
        doReturn(FilterExpression.alwaysTrue()).when(experiment1).getFilter();
        doReturn(experiment1).when(store).load(eq("foo"), any(Experiment.Builder.class));

        experiment2 = mock(Experiment.class);
        doReturn("bar").when(experiment2).getName();
        doReturn(FilterExpression.alwaysTrue()).when(experiment2).getFilter();
        doReturn(experiment2).when(store).load(eq("bar"), any(Experiment.Builder.class));

        doReturn(List.of(experiment1, experiment2))
                .when(store)
                .find(any(Filter.class), any(Experiment.BuilderFactory.class));

        experiments = Experiments.using(provider).using(strategy).build();
    }

    @Test
    public void testOnLoad() {
        experiments.get("bad_experiment");
        verifyNoMoreInteractions(strategy);

        experiments.get(experiment1.getName());
        verify(strategy).onLoad(eq(experiment1), any(CachingContext.class));

        reset(strategy);
        final Iterable<Experiment> result = experiments.find();
        final Iterator<Experiment> iterator = result.iterator();
        final Set<String> experimentNames =
                new HashSet<>(List.of(experiment1.getName(), experiment2.getName()));

        // no interactions until we actually iterate over results
        verifyNoMoreInteractions(strategy);

        assertTrue(
                "expected valid experiment name",
                experimentNames.remove(iterator.next().getName()));
        assertTrue(
                "expected valid experiment name",
                experimentNames.remove(iterator.next().getName()));
        verify(strategy, times(2)).onLoad(any(Experiment.class), any(CachingContext.class));

        assertFalse("should have no more results", iterator.hasNext());
    }

    @Test
    public void testOnSave() {
        experiments.save(experiment1);
        verify(strategy).onSave(eq(experiment1), any(CachingContext.class));
    }

    @Test
    public void testOnDelete() {
        experiments.delete(experiment1.getName());
        verify(strategy).onDelete(eq(experiment1.getName()), any(CachingContext.class));
    }

    @Test
    public void testOnRead() {
        // only triggered when reading from cache

        experiments.get(experiment1.getName());
        verify(strategy, never()).onCacheRead(anyString(), any(CachingContext.class));

        experiments.find();
        verify(strategy, never()).onCacheRead(any(CachingContext.class));

        // many read
        experiments.getActiveExperiments();
        verify(strategy).onCacheRead(any(CachingContext.class));
        verify(strategy, never()).onCacheRead(anyString(), any(CachingContext.class));

        reset(strategy);

        final Identity identity = mock(Identity.class);
        doReturn(AttributesMap.empty()).when(identity).computeAttributes();

        // single read
        experiments.getActiveTreatment(experiment1.getName(), identity);
        verify(strategy, never()).onCacheRead(any(CachingContext.class));
        verify(strategy).onCacheRead(anyString(), any(CachingContext.class));

        reset(strategy);

        // many read
        experiments.getActiveTreatments(identity);
        verify(strategy).onCacheRead(any(CachingContext.class));
        verify(strategy, never()).onCacheRead(anyString(), any(CachingContext.class));
    }
}
