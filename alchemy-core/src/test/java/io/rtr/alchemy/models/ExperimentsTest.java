package io.rtr.alchemy.models;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressing;
import io.rtr.alchemy.caching.CacheStrategy;
import io.rtr.alchemy.db.ExperimentsCache;
import io.rtr.alchemy.db.ExperimentsStoreProvider;
import io.rtr.alchemy.db.ExperimentsStore;
import io.rtr.alchemy.db.Filter;
import io.rtr.alchemy.filtering.FilterExpression;
import io.rtr.alchemy.identities.Attributes;
import io.rtr.alchemy.identities.AttributesMap;
import io.rtr.alchemy.identities.Identity;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.ValidationException;
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

    @Attributes({"foo", "bar"})
    private static class MyIdentity extends Identity {
        private final Set<String> attributes;

        public MyIdentity(String ... attributes) {
            this.attributes = Sets.newHashSet(attributes);
        }

        @Override
        public AttributesMap computeAttributes() {
            final AttributesMap.Builder builder = attributes();

            for (String name : attributes) {
                builder.put(name, true);
            }

            return builder.build();
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
        doReturn(AttributesMap.empty()).when(identity).computeAttributes();
        doReturn(FilterExpression.alwaysTrue()).when(experiment).getFilter();
        doReturn(ImmutableMap.of("foo", experiment))
            .when(cache)
            .getActiveExperiments();
        experiments.getActiveTreatment("foo", identity);
        verifyZeroInteractions(store);
        verify(cache).getActiveExperiments();
    }

    @Test
    public void testGetActiveTreatmentUnspecifiedAttributes() throws ValidationException {
        final MyIdentity identity1 = new MyIdentity("foo", "bar", "baz");
        final MyIdentity identity2 = new MyIdentity("baz");
        final MyIdentity identity3 = new MyIdentity("foo");

        final Experiment exp1 =
            experiments
                .create("exp1")
                .addTreatment("control")
                .allocate("control", 100)
                .setFilter(FilterExpression.of("baz"))
                .activate()
                .save();

        final Experiment exp2 =
            experiments
                .create("exp2")
                .addTreatment("control")
                .allocate("control", 100)
                .setFilter(FilterExpression.of("bar"))
                .activate()
                .save();

        doReturn(
            ImmutableMap.of(
                "exp1", exp1,
                "exp2", exp2
            )
        ).when(cache).getActiveExperiments();

        // baz was not specified in @Attributes
        assertNull(experiments.getActiveTreatment("exp1", identity1));
        assertNull(experiments.getActiveTreatment("exp1", identity2));

        // bar was specified in @Attributes
        assertNotNull(experiments.getActiveTreatment("exp2", identity1));
        assertNull(experiments.getActiveTreatment("exp2", identity2));
        assertNull(experiments.getActiveTreatment("exp2", identity3));
    }

    @Test
    public void testGetActiveTreatmentNoAttributes() throws ValidationException {
        final Identity identity = mock(Identity.class);
        doReturn(AttributesMap.empty()).when(identity).computeAttributes();

        final Experiment exp =
            experiments
                .create("exp")
                .addTreatment("control")
                .allocate("control", 100)
                .setFilter(FilterExpression.of("baz"))
                .activate()
                .save();

        doReturn(
            ImmutableMap.of(
                "exp", exp
            )
        ).when(cache).getActiveExperiments();

        // identity does not have @Attributes
        assertNull(experiments.getActiveTreatment("exp", identity));
    }

    @Test
    public void testGetActiveTreatments() {
        final Experiment experiment = mock(Experiment.class);
        final Identity identity = mock(Identity.class);
        doReturn(FilterExpression.alwaysTrue()).when(experiment).getFilter();
        doReturn(AttributesMap.empty()).when(identity).computeAttributes();
        doReturn(ImmutableMap.of("foo", experiment))
            .when(cache)
            .getActiveExperiments();
        experiments.getActiveTreatments(identity);
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
    public void testCreate() throws ValidationException {
        experiments.create("foo");
        verifyZeroInteractions(store);
        verifyZeroInteractions(cache);
    }

    @Test
    public void testSave() throws ValidationException {
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
