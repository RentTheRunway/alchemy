package com.rtr.alchemy.models;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AllocationsTest {
    @Test
    public void testEmptyAllocations() {
        Allocations allocations = new Allocations(Lists.<Allocation>newArrayList());
        assertEquals("unallocated size should be number of bins", Allocations.NUM_BINS, allocations.getUnallocatedSize());
        assertEquals("allocation size should be zero", 0, allocations.getSize());
        assertTrue("allocations should be empty", allocations.getAllocations().isEmpty());

        for (int i=0; i < Allocations.NUM_BINS; i++) {
            assertNull("unallocated treatment should return as null", allocations.getTreatment(i));
        }
    }

    @Test
    public void testSimpleAllocation() {
        List<Allocation> allocationList = Lists.newArrayList(
            new Allocation("control", 20),
            new Allocation("with_login", 30),
            new Allocation("without_login", 40)
        );

        Allocations allocations = new Allocations(allocationList);

        assertEquals("unallocated size should match", Allocations.NUM_BINS - 90, allocations.getUnallocatedSize());
        assertEquals("size should match", 90, allocations.getSize());
        assertEquals("allocations should match original input", allocationList, allocations.getAllocations());

        for (int i=0; i<20; i++) {
            assertEquals("first set of 20 should be control", allocationList.get(0).getTreatment(), allocations.getTreatment(i));
        }

        for (int i=20; i<50; i++) {
            assertEquals("second set of 30 should be with_login", allocationList.get(1).getTreatment(), allocations.getTreatment(i));
        }

        for (int i=50; i<90; i++) {
            assertEquals("third set of 40 should be without_login", allocationList.get(2).getTreatment(), allocations.getTreatment(i));
        }

        for (int i=90; i<Allocations.NUM_BINS - 90; i++) {
            assertNull("the rest should be unallocated", allocations.getTreatment(i));
        }
    }
}
