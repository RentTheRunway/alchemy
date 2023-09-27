package io.rtr.alchemy.testing.db;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import io.rtr.alchemy.db.ExperimentsStoreProvider;
import io.rtr.alchemy.db.Filter;
import io.rtr.alchemy.db.Ordering;
import io.rtr.alchemy.filtering.FilterExpression;
import io.rtr.alchemy.identities.Attributes;
import io.rtr.alchemy.identities.AttributesMap;
import io.rtr.alchemy.identities.Identity;
import io.rtr.alchemy.models.Allocations;
import io.rtr.alchemy.models.Experiment;
import io.rtr.alchemy.models.Experiments;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ValidationException;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * The purpose of this class is to provide a base class for testing whether an implementation of a
 * provider behaves correctly
 */
public abstract class ExperimentsStoreProviderTest {
    private Experiments experiments;

    protected abstract ExperimentsStoreProvider createProvider() throws Exception;

    protected abstract void resetStore();

    @Attributes({"test"})
    private static class TestIdentity extends Identity {
        private final String name;

        public TestIdentity(String name) {
            this.name = name;
        }

        @Override
        public long computeHash(int seed, Set<String> hashAttributes, AttributesMap attributes) {
            return identity(seed).putString(name).hash();
        }

        @Override
        public AttributesMap computeAttributes() {
            return attributes().put("test", true).build();
        }
    }

    @Before
    public void setUp() throws Exception {
        resetStore();
        final ExperimentsStoreProvider provider = createProvider();
        assertNotNull("provider cannot be null", provider);
        experiments = Experiments.using(provider).build();
    }

    @Test
    public void testInitialState() {
        assertFalse("there should be no experiments yet", experiments.find().iterator().hasNext());
        assertFalse(
                "there should be no experiments yet",
                experiments.find(Filter.criteria().build()).iterator().hasNext());
    }

    @Test
    public void testGetReturnsNull() {
        assertNull("should return null when experiment not found", experiments.get("foo"));
    }

    @Test
    public void testCreateExperiment() throws ValidationException {
        assertNull("should return null when experiment not found", experiments.get("foo"));

        experiments.create("foo").save();

        assertNotNull("did not find experiment", experiments.get("foo"));
    }

    @Test
    public void testDeleteExperiment() throws ValidationException {
        experiments.create("foo").save();

        assertNotNull("did not find experiment", experiments.get("foo"));

        experiments.delete("foo");

        assertNull("should return null when experiment not found", experiments.get("foo"));
    }

    @Test
    public void testFindExperimentFilterString() throws ValidationException {
        experiments.create("the_foo_experiment").setDescription("the bar description").save();

        assertFalse(
                "should not have found experiment",
                experiments.find(Filter.criteria().filter("control").build()).iterator().hasNext());

        assertTrue(
                "should be able to filter by name substring",
                experiments.find(Filter.criteria().filter("foo").build()).iterator().hasNext());

        assertTrue(
                "should be able to filter by description substring",
                experiments.find(Filter.criteria().filter("bar").build()).iterator().hasNext());
    }

    @Test
    public void testFindExperimentFilterRange() throws ValidationException {
        experiments.create("exp1").save();
        experiments.create("exp2").save();
        experiments.create("exp3").save();

        assertTrue("should have experiments", experiments.find().iterator().hasNext());

        assertEquals(3, Iterables.size(experiments.find(Filter.criteria().build())));

        assertEquals(2, Iterables.size(experiments.find(Filter.criteria().limit(2).build())));

        assertEquals(2, Iterables.size(experiments.find(Filter.criteria().offset(1).build())));

        assertEquals(
                1, Iterables.size(experiments.find(Filter.criteria().offset(1).limit(1).build())));
    }

    @Test
    public void testFindExperimentFilterOrdering() throws ValidationException {
        final Experiment fooExp = experiments.create("foo").setDescription("a").save();
        final Experiment zooExp = experiments.create("zoo").setDescription("b").save();
        final Experiment barExp = experiments.create("bar").setDescription("c").save();

        assertTrue("should have experiments", experiments.find().iterator().hasNext());

        assertEquals(3, Iterables.size(experiments.find(Filter.criteria().build())));

        assertEquals(
                Lists.newArrayList(barExp, fooExp, zooExp),
                Lists.newArrayList(
                        experiments.find(
                                Filter.criteria()
                                        .ordering(
                                                Ordering.newBuilder()
                                                        .orderBy(Ordering.Field.NAME)
                                                        .build())
                                        .build())));

        assertEquals(
                Lists.newArrayList(zooExp, fooExp, barExp),
                Lists.newArrayList(
                        experiments.find(
                                Filter.criteria()
                                        .ordering(
                                                Ordering.newBuilder()
                                                        .orderBy(
                                                                Ordering.Field.NAME,
                                                                Ordering.Direction.DESCENDING)
                                                        .build())
                                        .build())));

        assertEquals(
                Lists.newArrayList(fooExp, zooExp, barExp),
                Lists.newArrayList(
                        experiments.find(
                                Filter.criteria()
                                        .ordering(
                                                Ordering.newBuilder()
                                                        .orderBy(Ordering.Field.DESCRIPTION)
                                                        .build())
                                        .build())));

        assertEquals(
                Lists.newArrayList(barExp, zooExp, fooExp),
                Lists.newArrayList(
                        experiments.find(
                                Filter.criteria()
                                        .ordering(
                                                Ordering.newBuilder()
                                                        .orderBy(
                                                                Ordering.Field.DESCRIPTION,
                                                                Ordering.Direction.DESCENDING)
                                                        .build())
                                        .build())));
    }

    @Test
    public void testGetActiveTreatment() throws ValidationException {
        experiments
                .create("foo")
                .addTreatment("control")
                .setHashAttributes()
                .allocate("control", 100)
                .save();

        assertNull(
                "no active treatment should be returned for deactivated experiment",
                experiments.getActiveTreatment("foo", new TestIdentity("foo")));

        experiments.get("foo").activate().save();

        final Identity identity = new TestIdentity("test");

        assertEquals(
                "expected control treatment",
                "control",
                experiments.getActiveTreatment("foo", identity).getName());

        experiments.get("foo").setFilter(FilterExpression.of("test")).save();

        assertEquals(
                "expected control treatment",
                "control",
                experiments.getActiveTreatment("foo", identity).getName());

        experiments.get("foo").setFilter(FilterExpression.of("bar")).save();

        assertNull(
                "no active treatment should be returned for experiment intended for different identity type",
                experiments.getActiveTreatment("foo", identity));
    }

    @Test
    public void testGetActiveTreatments() throws ValidationException {
        final Identity identity = new TestIdentity("test");

        experiments
                .create("foo")
                .addTreatment("control")
                .allocate("control", 100)
                .setFilter(FilterExpression.of("test"))
                .save();

        experiments
                .create("bar")
                .addTreatment("control")
                .allocate("control", 100)
                .setFilter(FilterExpression.of("test"))
                .save();

        assertTrue("no active experiments", experiments.getActiveTreatments(identity).isEmpty());

        experiments.get("foo").activate().save();

        assertEquals(
                "should have one experiment", 1, experiments.getActiveTreatments(identity).size());
        assertEquals(
                "wrong experiment",
                "foo",
                experiments.getActiveTreatments(identity).keySet().iterator().next().getName());

        experiments.get("bar").activate().save();

        assertEquals(
                "should have two experiments", 2, experiments.getActiveTreatments(identity).size());

        experiments.get("bar").setFilter(FilterExpression.of("bar")).save();

        assertEquals(
                "should have one because of identity type",
                1,
                experiments.getActiveTreatments(identity).size());
    }

    @Test
    public void testGetActiveExperiments() throws ValidationException {
        experiments.create("foo").save();

        assertFalse(
                "should have no active experiments",
                experiments.getActiveExperiments().iterator().hasNext());

        experiments.get("foo").activate().save();

        assertTrue(
                "should have an active experiment",
                experiments.getActiveExperiments().iterator().hasNext());
    }

    @Test
    public void testExperimentObjectReference() throws ValidationException {
        final Experiment obj1 =
                experiments
                        .create("foo")
                        .addTreatment("control")
                        .allocate("control", Allocations.NUM_BINS)
                        .activate()
                        .save();

        final Experiment obj2 = experiments.get("foo");

        assertFalse(
                "saved experiment object reference should not be same object reference from get()",
                obj1 == obj2);

        final Experiment obj3 = experiments.find().iterator().next();

        assertFalse(
                "saved experiment object reference should not be same object reference from find()",
                obj1 == obj3);

        final Experiment obj4 = experiments.getActiveExperiments().iterator().next();

        assertFalse(
                "saved experiment object reference should not be same object reference from getActiveExperiments()",
                obj1 == obj4);

        final Identity identity = mock(Identity.class);
        doReturn(AttributesMap.empty()).when(identity).computeAttributes();
        final Experiment obj5 =
                experiments.getActiveTreatments(identity).keySet().iterator().next();

        assertFalse(
                "saved experiment object reference should not be same object reference from getActiveTreatments()",
                obj1 == obj5);
    }
}
