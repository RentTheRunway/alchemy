package com.rtr.alchemy.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.rtr.alchemy.identities.Identity;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ExperimentTest {
    private final Identity identity = mock(Identity.class);
    private Experiments experiments;

    @Before
    public void setUp() {
        experiments = mock(Experiments.class);
    }

    @Test
    public void testEqualsHashCode() {
        EqualsVerifier
            .forClass(Experiment.class)
            .withPrefabValues(Experiments.class, mock(Experiments.class), mock(Experiments.class))
            .suppress(Warning.STRICT_INHERITANCE)
            .verify();
    }

    @Test
    public void testAddTreatment() {
        final Treatment treatment = new Treatment("bar");
        final Experiment experiment =
            new Experiment(null, "foo")
                .addTreatment("bar");

        assertEquals(treatment, experiment.getTreatments().iterator().next());
    }

    @Test
    public void testAddOverride() {
        final Treatment treatment = new Treatment("bar");
        final TreatmentOverride override = new TreatmentOverride("override", 0, treatment);
        final Experiment experiment =
            new Experiment(null, "foo")
                .addTreatment("bar")
                .addOverride("override", "bar", identity);

        assertEquals(treatment, experiment.getTreatments().get(0));
        assertEquals(override, experiment.getOverrides().get(0));
    }

    @Test
    public void testGetOverride() {
        final Treatment treatment = new Treatment("bar");
        final TreatmentOverride override = new TreatmentOverride("override", 0, treatment);
        final Experiment experiment =
            new Experiment(null, "foo")
                .addTreatment("bar")
                .addOverride("override", "bar", identity);

        assertEquals(treatment, experiment.getTreatments().get(0));
        assertEquals(override, experiment.getOverride(override.getName()));
    }

    @Test
    public void testClearTreatments() {
        final Experiment experiment =
            new Experiment(null, "foo")
                .addTreatment("bar")
                .allocate("bar", 10)
                .addOverride("override", "bar", identity);

        assertEquals(1, experiment.getTreatments().size());
        assertEquals(1, experiment.getAllocations().size());
        assertEquals(1, experiment.getOverrides().size());

        experiment.clearTreatments();

        assertEquals(0, experiment.getTreatments().size());
        assertEquals(0, experiment.getAllocations().size());
        assertEquals(0, experiment.getOverrides().size());
    }

    @Test
    public void testClearOverrides() {
        final Experiment experiment =
            new Experiment(null, "foo")
                .addTreatment("bar")
                .addOverride("override", "bar", identity);

        assertEquals(1, experiment.getTreatments().size());
        assertEquals(1, experiment.getOverrides().size());

        experiment.clearOverrides();

        assertEquals(1, experiment.getTreatments().size());
        assertEquals(0, experiment.getOverrides().size());
    }

    @Test
    public void testDeallocateAll() {
        final Experiment experiment =
            new Experiment(null, "foo")
                .addTreatment("bar")
                .allocate("bar", 10);

        assertEquals(1, experiment.getTreatments().size());
        assertEquals(1, experiment.getAllocations().size());

        experiment.deallocateAll();

        assertEquals(1, experiment.getTreatments().size());
        assertEquals(0, experiment.getAllocations().size());
    }

    @Test
    public void testRemoveTreatment() {
        doReturn(0L).when(identity).computeHash(anyInt());

        final Experiment experiment =
            new Experiment(null, "foo")
                .addTreatment("control")
                .addTreatment("cake")
                .addTreatment("pie")
                .allocate("control", 10)
                .allocate("cake", 10)
                .allocate("pie", 10)
                .allocate("control", 10)
                .allocate("cake", 10)
                .allocate("pie", 10)
                .addOverride("control_override", "control", identity);

        List<Treatment> treatments = Lists.newArrayList(experiment.getTreatments());
        assertEquals("should contain expected number of treatments", 3, treatments.size());

        List<Allocation> allocations = Lists.newArrayList(experiment.getAllocations());
        assertEquals("should contain expected number of allocations", 6, allocations.size());

        List<TreatmentOverride> overrides = Lists.newArrayList(experiment.getOverrides());
        assertEquals("should contain override", 1, overrides.size());

        experiment.removeTreatment("pie");

        treatments = Lists.newArrayList(experiment.getTreatments());
        assertEquals("should contain fewer treatments", 2, treatments.size());

        allocations = Lists.newArrayList(experiment.getAllocations());
        assertEquals("should contain fewer allocations", 4, allocations.size());

        overrides = Lists.newArrayList(experiment.getOverrides());
        assertEquals("should contain same override", 1, overrides.size());

        experiment.removeTreatment("control");

        treatments = Lists.newArrayList(experiment.getTreatments());
        assertEquals("should contain fewer treatments", 1, treatments.size());

        allocations = Lists.newArrayList(experiment.getAllocations());
        // (it's 1 allocation rather than 2, because the remaining 'cake' allocations merge into one)
        assertEquals("should contain fewer allocations", 1, allocations.size());

        overrides = Lists.newArrayList(experiment.getOverrides());
        assertEquals("should contain no overrides", 0, overrides.size());
    }

    @Test
    public void testImmutableAllocations() {
        final  Experiment experiment =
            new Experiment(null, "experiment")
                .addTreatment("foo")
                .allocate("foo", 10);

        assertTrue(ImmutableList.class.isAssignableFrom(experiment.getAllocations().getClass()));
    }

    @Test
    public void testImmutableTreatments() {
        final  Experiment experiment =
            new Experiment(null, "experiment")
                .addTreatment("foo");

        assertTrue(ImmutableList.class.isAssignableFrom(experiment.getTreatments().getClass()));
    }

    @Test
    public void testImmutableOverrides() {
        final  Experiment experiment =
            new Experiment(null, "experiment")
                .addTreatment("foo")
                .addOverride("override", "foo", identity);

        assertTrue(ImmutableList.class.isAssignableFrom(experiment.getOverrides().getClass()));
    }

    @Test
    public void testCopyOf() {
        assertNull(Experiment.copyOf(null));

        final Experiment original =
            new Experiment(null, "experiment")
                .activate()
                .addTreatment("foo")
                .addOverride("override", "foo", identity)
                .allocate("foo", 10);

        final Experiment copy = Experiment.copyOf(original);

        assertFalse(original == copy);
        assertFalse(original.getTreatments().get(0) == copy.getTreatments().get(0));
        assertFalse(original.getAllocations().get(0) == copy.getAllocations().get(0));
        assertFalse(original.getOverrides().get(0) == copy.getOverrides().get(0));
        assertTrue(copy.getAllocations().get(0).getTreatment() == copy.getTreatments().get(0));
        assertTrue(copy.getOverrides().get(0).getTreatment() == copy.getTreatments().get(0));
    }

    @Test
    public void testSave() {
        final Experiment experiment = new Experiment(experiments, "foo").save();
        verify(experiments).save(eq(experiment));
    }

    @Test
    public void testDelete() {
        final Experiment experiment = new Experiment(experiments, "foo");
        experiment.delete();
        verify(experiments).delete(eq(experiment.getName()));
    }
}
