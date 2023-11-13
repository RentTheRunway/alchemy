package io.rtr.alchemy.caching;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import io.rtr.alchemy.db.ExperimentsCache;
import io.rtr.alchemy.db.ExperimentsStore;
import io.rtr.alchemy.db.ExperimentsStoreProvider;
import io.rtr.alchemy.db.Filter;
import io.rtr.alchemy.models.Experiment;
import io.rtr.alchemy.models.Experiments;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BasicCacheStrategyTest {
    private ExperimentsCache cache;
    private Experiments experiments;
    private Experiment activeExperiment;
    private Experiment inactiveExperiment;

    @Before
    public void setUp() {
        final CacheStrategy strategy = new BasicCacheStrategy();
        final ExperimentsStoreProvider provider = mock(ExperimentsStoreProvider.class);
        final ExperimentsStore store = mock(ExperimentsStore.class);
        cache = mock(ExperimentsCache.class);
        doReturn(store).when(provider).getStore();
        doReturn(cache).when(provider).getCache();

        activeExperiment = mock(Experiment.class);
        doReturn("foo").when(activeExperiment).getName();
        doReturn(true).when(activeExperiment).isActive();
        doReturn(activeExperiment).when(store).load(eq("foo"), any(Experiment.Builder.class));

        inactiveExperiment = mock(Experiment.class);
        doReturn("bar").when(inactiveExperiment).getName();
        doReturn(false).when(inactiveExperiment).isActive();
        doReturn(inactiveExperiment).when(store).load(eq("bar"), any(Experiment.Builder.class));

        doReturn(List.of(activeExperiment, inactiveExperiment))
                .when(store)
                .find(any(Filter.class), any(Experiment.BuilderFactory.class));

        experiments = Experiments.using(provider).using(strategy).build();
    }

    @Test
    public void testSave() {
        experiments.save(activeExperiment);
        verify(cache).update(eq(activeExperiment));

        experiments.save(inactiveExperiment);
        verify(cache).delete(eq(inactiveExperiment.getName()));
    }

    @Test
    public void testLoad() {
        experiments.get(activeExperiment.getName());
        verify(cache).update(eq(activeExperiment));

        experiments.get(inactiveExperiment.getName());
        verify(cache).delete(eq(inactiveExperiment.getName()));

        reset(cache);

        final Iterable<Experiment> result = experiments.find();
        final Iterator<Experiment> iterator = result.iterator();

        final Set<String> experimentNames =
                new HashSet<>(List.of(activeExperiment.getName(), inactiveExperiment.getName()));
        assertTrue(
                "expected valid experiment name",
                experimentNames.remove(iterator.next().getName()));
        assertTrue(
                "expected valid experiment name",
                experimentNames.remove(iterator.next().getName()));
        verify(cache).update(eq(activeExperiment));
        verify(cache).delete(eq(inactiveExperiment.getName()));
    }

    @Test
    public void testDelete() {
        experiments.delete(inactiveExperiment.getName());

        verify(cache).delete(eq(inactiveExperiment.getName()));
    }
}
