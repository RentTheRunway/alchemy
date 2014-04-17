package com.rtr.alchemy.models;

import com.google.common.collect.Lists;
import com.rtr.alchemy.db.ExperimentsStore;
import com.rtr.alchemy.identities.Identity;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ExperimentTest {
    @Test
    public void testEqualsHashCode() {
        EqualsVerifier
            .forClass(Experiment.class)
            .suppress(Warning.STRICT_INHERITANCE)
            .verify();
    }

    @Test
    public void testRemoveTreatment() {
        final ExperimentsStore store = mock(ExperimentsStore.class);
        final Identity identity = mock(Identity.class);

        doReturn("user").when(identity).getType();
        doReturn(0L).when(identity).getHash(anyInt());

        final Experiment experiment =
            new Experiment(store, "foo")
                .addTreatment("control")
                .addTreatment("cake")
                .addTreatment("pie")
                .allocate("control", 10)
                .allocate("cake", 10)
                .allocate("pie", 10)
                .allocate("control", 10)
                .allocate("cake", 10)
                .allocate("pie", 10)
                .addOverride("control", identity);

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
}
