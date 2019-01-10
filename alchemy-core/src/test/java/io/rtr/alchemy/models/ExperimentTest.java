package io.rtr.alchemy.models;

import com.google.common.collect.Lists;
import io.rtr.alchemy.filtering.FilterExpression;
import io.rtr.alchemy.identities.AttributesMap;
import io.rtr.alchemy.identities.Identity;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
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
            .withPrefabValues(FilterExpression.class, mock(FilterExpression.class), mock(FilterExpression.class))
            .suppress(Warning.STRICT_INHERITANCE)
            .verify();
    }

    @Test
    public void testAddTreatment() {
        final Treatment treatment = new Treatment(11111);
        final Experiment experiment =
            new Experiment(null, "my_experiment")
                .addTreatment(11111);

        assertEquals(treatment, experiment.getTreatments().iterator().next());
    }

    @Test
    public void testAddOverride() {
        final Treatment treatment = new Treatment(11111);
        final TreatmentOverride override = new TreatmentOverride("override", FilterExpression.alwaysTrue(), treatment);
        final Experiment experiment =
            new Experiment(null, "my_experiment")
                .addTreatment(11111)
                .addOverride("override", 11111, "true");

        assertEquals(treatment, experiment.getTreatments().get(0));
        assertEquals(override, experiment.getOverrides().get(0));
    }

    @Test
    public void testGetOverride() {
        final Treatment treatment = new Treatment(11111);
        final TreatmentOverride override = new TreatmentOverride("override", FilterExpression.alwaysTrue(), treatment);
        final Experiment experiment =
            new Experiment(null, "my_experiment")
                .addTreatment(11111)
                .addOverride("override", 11111, "true");

        assertEquals(treatment, experiment.getTreatments().get(0));
        assertEquals(override, experiment.getOverride(override.getName()));
    }

    @Test
    public void testClearTreatments() {
        final Experiment experiment =
            new Experiment(null, "my_experiment")
                .addTreatment(11111)
                .allocate(11111, 10)
                .addOverride("override", 11111, "true");

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
            new Experiment(null, "my_experiment")
                .addTreatment(11111)
                .addOverride("override", 11111, "true");

        assertEquals(1, experiment.getTreatments().size());
        assertEquals(1, experiment.getOverrides().size());

        experiment.clearOverrides();

        assertEquals(1, experiment.getTreatments().size());
        assertEquals(0, experiment.getOverrides().size());
    }

    @Test
    public void testDeallocateAll() {
        final Experiment experiment =
            new Experiment(null, "my_experiment")
                .addTreatment(11111)
                .allocate(11111, 10);

        assertEquals(1, experiment.getTreatments().size());
        assertEquals(1, experiment.getAllocations().size());

        experiment.deallocateAll();

        assertEquals(1, experiment.getTreatments().size());
        assertEquals(0, experiment.getAllocations().size());
    }

    @Test
    public void testRemoveTreatment() {
        doReturn(0L).when(identity).computeHash(anyInt(), Mockito.<Set<String>>any(), any(AttributesMap.class));

        final Experiment experiment =
            new Experiment(null, "my_experiment")
                .addTreatment(1)
                .addTreatment(2)
                .addTreatment(3)
                .allocate(1, 10)
                .allocate(2, 10)
                .allocate(3, 10)
                .allocate(1, 10)
                .allocate(2, 10)
                .allocate(3, 10)
                .addOverride("control_override", 1, "true");

        List<Treatment> treatments = Lists.newArrayList(experiment.getTreatments());
        assertEquals("should contain expected number of treatments", 3, treatments.size());

        List<Allocation> allocations = Lists.newArrayList(experiment.getAllocations());
        assertEquals("should contain expected number of allocations", 6, allocations.size());

        List<TreatmentOverride> overrides = Lists.newArrayList(experiment.getOverrides());
        assertEquals("should contain override", 1, overrides.size());

        experiment.removeTreatment(3);

        treatments = Lists.newArrayList(experiment.getTreatments());
        assertEquals("should contain fewer treatments", 2, treatments.size());

        allocations = Lists.newArrayList(experiment.getAllocations());
        assertEquals("should contain fewer allocations", 4, allocations.size());

        overrides = Lists.newArrayList(experiment.getOverrides());
        assertEquals("should contain same override", 1, overrides.size());

        experiment.removeTreatment(1);

        treatments = Lists.newArrayList(experiment.getTreatments());
        assertEquals("should contain fewer treatments", 1, treatments.size());

        allocations = Lists.newArrayList(experiment.getAllocations());
        // (it's 1 allocation rather than 2, because the remaining 'cake' allocations merge into one)
        assertEquals("should contain fewer allocations", 1, allocations.size());

        overrides = Lists.newArrayList(experiment.getOverrides());
        assertEquals("should contain no overrides", 0, overrides.size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableAllocations() {
        final  Experiment experiment =
            new Experiment(null, "experiment")
                .addTreatment(22222)
                .allocate(22222, 10);

        experiment.getAllocations().add(mock(Allocation.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableTreatments() {
        final  Experiment experiment =
            new Experiment(null, "experiment")
                .addTreatment(22222);

        experiment.getTreatments().add(mock(Treatment.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableOverrides() {
        final  Experiment experiment =
            new Experiment(null, "experiment")
                .addTreatment(22222)
                .addOverride("override", 22222, "true");

        experiment.getOverrides().add(mock(TreatmentOverride.class));
    }

    @Test
    public void testCopyOf() {
        assertNull(Experiment.copyOf(null));

        final Experiment original =
            new Experiment(null, "experiment")
                .activate()
                .addTreatment(22222)
                .addOverride("override", 22222, "true")
                .allocate(22222, 10);

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
        final Experiment experiment = new Experiment(experiments, "my_experiment").save();
        verify(experiments).save(eq(experiment));
    }

    @Test
    public void testDelete() {
        final Experiment experiment = new Experiment(experiments, "my_experiment");
        experiment.delete();
        verify(experiments).delete(eq(experiment.getName()));
    }
}
