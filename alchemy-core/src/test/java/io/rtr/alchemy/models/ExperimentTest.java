package io.rtr.alchemy.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.rtr.alchemy.filtering.FilterExpression;
import io.rtr.alchemy.identities.AttributesMap;
import io.rtr.alchemy.identities.Identity;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ValidationException;

public class ExperimentTest {
    private final Identity identity = mock(Identity.class);
    private Experiments experiments;

    @Before
    public void setUp() {
        experiments = mock(Experiments.class);
    }

    @Test
    public void testEqualsHashCode() {
        EqualsVerifier.forClass(Experiment.class)
                .withPrefabValues(
                        Experiments.class, mock(Experiments.class), mock(Experiments.class))
                .withPrefabValues(
                        FilterExpression.class,
                        mock(FilterExpression.class),
                        mock(FilterExpression.class))
                .suppress(Warning.STRICT_INHERITANCE)
                .withOnlyTheseFields("name")
                .verify();
    }

    @Test
    public void testAddTreatment() throws ValidationException {
        final Treatment treatment = new Treatment("bar");
        final Experiment experiment = new Experiment(null, "foo").addTreatment("bar");

        assertEquals(treatment, experiment.getTreatments().iterator().next());
    }

    @Test
    public void testAddOverride() throws ValidationException {
        final Treatment treatment = new Treatment("bar");
        final TreatmentOverride override =
                new TreatmentOverride("override", FilterExpression.alwaysTrue(), treatment);
        final Experiment experiment =
                new Experiment(null, "foo")
                        .addTreatment("bar")
                        .addOverride("override", "bar", "true");

        assertEquals(treatment, experiment.getTreatments().get(0));
        assertEquals(override, experiment.getOverrides().get(0));
    }

    @Test
    public void testGetOverride() throws ValidationException {
        final Treatment treatment = new Treatment("bar");
        final TreatmentOverride override =
                new TreatmentOverride("override", FilterExpression.alwaysTrue(), treatment);
        final Experiment experiment =
                new Experiment(null, "foo")
                        .addTreatment("bar")
                        .addOverride("override", "bar", "true");

        assertEquals(treatment, experiment.getTreatments().get(0));
        assertEquals(override, experiment.getOverride(override.getName()));
    }

    @Test
    public void testClearTreatments() throws ValidationException {
        final Experiment experiment =
                new Experiment(null, "foo")
                        .addTreatment("bar")
                        .allocate("bar", 10)
                        .addOverride("override", "bar", "true");

        assertEquals(1, experiment.getTreatments().size());
        assertEquals(1, experiment.getAllocations().size());
        assertEquals(1, experiment.getOverrides().size());

        experiment.clearTreatments();

        assertEquals(0, experiment.getTreatments().size());
        assertEquals(0, experiment.getAllocations().size());
        assertEquals(0, experiment.getOverrides().size());
    }

    @Test
    public void testClearOverrides() throws ValidationException {
        final Experiment experiment =
                new Experiment(null, "foo")
                        .addTreatment("bar")
                        .addOverride("override", "bar", "true");

        assertEquals(1, experiment.getTreatments().size());
        assertEquals(1, experiment.getOverrides().size());

        experiment.clearOverrides();

        assertEquals(1, experiment.getTreatments().size());
        assertEquals(0, experiment.getOverrides().size());
    }

    @Test
    public void testDeallocateAll() throws ValidationException {
        final Experiment experiment =
                new Experiment(null, "foo").addTreatment("bar").allocate("bar", 10);

        assertEquals(1, experiment.getTreatments().size());
        assertEquals(1, experiment.getAllocations().size());

        experiment.deallocateAll();

        assertEquals(1, experiment.getTreatments().size());
        assertEquals(0, experiment.getAllocations().size());
    }

    @Test
    public void testRemoveTreatment() throws ValidationException {
        doReturn(0L)
                .when(identity)
                .computeHash(anyInt(), any(), any(AttributesMap.class));

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
                        .addOverride("control_override", "control", "true");

        List<Treatment> treatments = new ArrayList<>(experiment.getTreatments());
        assertEquals("should contain expected number of treatments", 3, treatments.size());

        List<Allocation> allocations = new ArrayList<>(experiment.getAllocations());
        assertEquals("should contain expected number of allocations", 6, allocations.size());

        List<TreatmentOverride> overrides = new ArrayList<>(experiment.getOverrides());
        assertEquals("should contain override", 1, overrides.size());

        experiment.removeTreatment("pie");

        treatments = new ArrayList<>(experiment.getTreatments());
        assertEquals("should contain fewer treatments", 2, treatments.size());

        allocations = new ArrayList<>(experiment.getAllocations());
        assertEquals("should contain fewer allocations", 4, allocations.size());

        overrides = new ArrayList<>(experiment.getOverrides());
        assertEquals("should contain same override", 1, overrides.size());

        experiment.removeTreatment("control");

        treatments = new ArrayList<>(experiment.getTreatments());
        assertEquals("should contain fewer treatments", 1, treatments.size());

        allocations = new ArrayList<>(experiment.getAllocations());
        // (it's 1 allocation rather than 2, because the remaining 'cake' allocations merge into
        // one)
        assertEquals("should contain fewer allocations", 1, allocations.size());

        overrides = new ArrayList<>(experiment.getOverrides());
        assertEquals("should contain no overrides", 0, overrides.size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableAllocations() throws ValidationException {
        final Experiment experiment =
                new Experiment(null, "experiment").addTreatment("foo").allocate("foo", 10);

        experiment.getAllocations().add(mock(Allocation.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableTreatments() throws ValidationException {
        final Experiment experiment = new Experiment(null, "experiment").addTreatment("foo");

        experiment.getTreatments().add(mock(Treatment.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableOverrides() throws ValidationException {
        final Experiment experiment =
                new Experiment(null, "experiment")
                        .addTreatment("foo")
                        .addOverride("override", "foo", "true");

        experiment.getOverrides().add(mock(TreatmentOverride.class));
    }

    @Test
    public void testCopyOf() throws ValidationException {
        assertNull(Experiment.copyOf(null));

        final Experiment original =
                new Experiment(null, "experiment")
                        .activate()
                        .addTreatment("foo")
                        .addOverride("override", "foo", "true")
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
    public void testSave() throws ValidationException {
        final Experiment experiment = new Experiment(experiments, "foo").save();
        verify(experiments).save(experiment);
    }

    @Test
    public void testDelete() throws ValidationException {
        final Experiment experiment = new Experiment(experiments, "foo");
        experiment.delete();
        verify(experiments).delete(experiment.getName());
    }

    @Test
    public void testValidName() throws ValidationException {
        String name = "My1stExperiment";
        final Experiment experiment = new Experiment(experiments, name);
        assertEquals(experiment.getName(), name);
    }

    @Test(expected = ValidationException.class)
    public void testInvalidName() throws ValidationException {
        String name = "(not a valid name)";
        final Experiment experiment = new Experiment(experiments, name);
        experiment.validateName();
    }
}
