package com.rtr.alchemy.models;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.rtr.alchemy.caching.CacheStrategy;
import com.rtr.alchemy.db.ExperimentsCache;
import com.rtr.alchemy.db.ExperimentsStoreProvider;
import com.rtr.alchemy.db.ExperimentsStore;
import com.rtr.alchemy.db.Filter;
import com.rtr.alchemy.identities.Identity;
import com.rtr.alchemy.identities.Segments;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

    @Segments({"foo", "bar"})
    private static class MyIdentity extends Identity {
        private final Set<String> segments;

        public MyIdentity(String ... segments) {
            this.segments = Sets.newHashSet(segments);
        }

        @Override
        public long computeHash(int seed) {
            return 0;
        }

        @Override
        public Set<String> computeSegments() {
            return segments;
        }
    }

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
    public void testGetActiveTreatmentUnspecifiedSegments() {
        final MyIdentity identity1 = new MyIdentity("foo", "bar", "baz");
        final MyIdentity identity2 = new MyIdentity("baz");
        final MyIdentity identity3 = new MyIdentity("foo");

        final Experiment exp1 =
            experiments
                .create("exp1")
                .addTreatment("control")
                .allocate("control", 100)
                .setSegments("baz")
                .activate()
                .save();

        final Experiment exp2 =
            experiments
                .create("exp2")
                .addTreatment("control")
                .allocate("control", 100)
                .setSegments("bar")
                .activate()
                .save();

        doReturn(
            ImmutableMap.of(
                "exp1", exp1,
                "exp2", exp2
            )
        ).when(cache).getActiveExperiments();

        // baz was not specified in @Segments
        assertNull(experiments.getActiveTreatment("exp1", identity1));
        assertNull(experiments.getActiveTreatment("exp1", identity2));

        // bar was specified in @Segments
        assertNotNull(experiments.getActiveTreatment("exp2", identity1));
        assertNull(experiments.getActiveTreatment("exp2", identity2));
        assertNull(experiments.getActiveTreatment("exp2", identity3));
    }

    @Test
    public void testGetActiveTreatmentNoSegments() {
        final Identity identity = mock(Identity.class);
        doReturn(Sets.newHashSet("baz")).when(identity).computeSegments();

        final Experiment exp =
            experiments
                .create("exp")
                .addTreatment("control")
                .allocate("control", 100)
                .setSegments("baz")
                .activate()
                .save();

        doReturn(
            ImmutableMap.of(
                "exp", exp
            )
        ).when(cache).getActiveExperiments();

        // identity does not have @Segments
        assertNull(experiments.getActiveTreatment("exp", identity));
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
