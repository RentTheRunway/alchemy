package io.rtr.alchemy.testing.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import javax.validation.ValidationException;

/**
 * The purpose of this class is to provide a base class for testing whether an implementation of a
 * provider behaves correctly
 */
public abstract class ExperimentsStoreProviderTest {
    private Experiments experiments;

    protected abstract ExperimentsStoreProvider createProvider();

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

    @BeforeEach
    public void setUp() {
        resetStore();
        final ExperimentsStoreProvider provider = createProvider();
        assertNotNull(provider, "provider cannot be null");
        experiments = Experiments.using(provider).build();
    }

    @Test
    public void testInitialState() {
        assertFalse(experiments.find().iterator().hasNext(), "there should be no experiments yet");
        assertFalse(
                experiments.find(Filter.criteria().build()).iterator().hasNext(),
                "there should be no experiments yet");
    }

    @Test
    public void testGetReturnsNull() {
        assertNull(experiments.get("foo"), "should return null when experiment not found");
    }

    @Test
    public void testCreateExperiment() throws ValidationException {
        assertNull(experiments.get("foo"), "should return null when experiment not found");

        experiments.create("foo").save();

        assertNotNull(experiments.get("foo"), "did not find experiment");
    }

    @Test
    public void testDeleteExperiment() throws ValidationException {
        experiments.create("foo").save();

        assertNotNull(experiments.get("foo"), "did not find experiment");

        experiments.delete("foo");

        assertNull(experiments.get("foo"), "should return null when experiment not found");
    }

    @Test
    public void testFindExperimentFilterString() throws ValidationException {
        experiments.create("the_foo_experiment").setDescription("the bar description").save();

        assertFalse(
                experiments.find(Filter.criteria().filter("control").build()).iterator().hasNext(),
                "should not have found experiment");

        assertTrue(
                experiments.find(Filter.criteria().filter("foo").build()).iterator().hasNext(),
                "should be able to filter by name substring");

        assertTrue(
                experiments.find(Filter.criteria().filter("bar").build()).iterator().hasNext(),
                "should be able to filter by description substring");
    }

    @Test
    public void testFindExperimentFilterRange() throws ValidationException {
        experiments.create("exp1").save();
        experiments.create("exp2").save();
        experiments.create("exp3").save();

        assertTrue(experiments.find().iterator().hasNext(), "should have experiments");

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

        assertTrue(experiments.find().iterator().hasNext(), "should have experiments");

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
                experiments.getActiveTreatment("foo", new TestIdentity("foo")),
                "no active treatment should be returned for deactivated experiment");

        experiments.get("foo").activate().save();

        final Identity identity = new TestIdentity("test");

        assertEquals(
                "control",
                experiments.getActiveTreatment("foo", identity).getName(),
                "expected control treatment");

        experiments.get("foo").setFilter(FilterExpression.of("test")).save();

        assertEquals(
                "control",
                experiments.getActiveTreatment("foo", identity).getName(),
                "expected control treatment");

        experiments.get("foo").setFilter(FilterExpression.of("bar")).save();

        assertNull(
                experiments.getActiveTreatment("foo", identity),
                "no active treatment should be returned for experiment intended for different identity type");
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

        assertTrue(experiments.getActiveTreatments(identity).isEmpty(), "no active experiments");

        experiments.get("foo").activate().save();

        assertEquals(
                1, experiments.getActiveTreatments(identity).size(), "should have one experiment");
        assertEquals(
                "foo",
                experiments.getActiveTreatments(identity).keySet().iterator().next().getName(),
                "wrong experiment");

        experiments.get("bar").activate().save();

        assertEquals(
                2, experiments.getActiveTreatments(identity).size(), "should have two experiments");

        experiments.get("bar").setFilter(FilterExpression.of("bar")).save();

        assertEquals(
                1,
                experiments.getActiveTreatments(identity).size(),
                "should have one because of identity type");
    }

    @Test
    public void testGetActiveExperiments() throws ValidationException {
        experiments.create("foo").save();

        assertFalse(
                experiments.getActiveExperiments().iterator().hasNext(),
                "should have no active experiments");

        experiments.get("foo").activate().save();

        assertTrue(
                experiments.getActiveExperiments().iterator().hasNext(),
                "should have an active experiment");
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

        assertNotSame(
                obj1,
                obj2,
                "saved experiment object reference should not be same object reference from get()");

        final Experiment obj3 = experiments.find().iterator().next();

        assertNotSame(
                obj1,
                obj3,
                "saved experiment object reference should not be same object reference from find()");

        final Experiment obj4 = experiments.getActiveExperiments().iterator().next();

        assertNotSame(
                obj1,
                obj4,
                "saved experiment object reference should not be same object reference from getActiveExperiments()");

        final Identity identity = mock(Identity.class);
        doReturn(AttributesMap.empty()).when(identity).computeAttributes();
        final Experiment obj5 =
                experiments.getActiveTreatments(identity).keySet().iterator().next();

        assertNotSame(
                obj1,
                obj5,
                "saved experiment object reference should not be same object reference from getActiveTreatments()");
    }
}
