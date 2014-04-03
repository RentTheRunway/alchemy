package com.rtr.alchemy.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents allocations of treatments for an experiment
 */
public class Allocations {
    public static final int NUM_BINS = 100;
    private final List<Allocation> allocations;
    private final Byte[] allocationMap;
    private final String[] treatmentsMap;
    private final int size;

    public Allocations(Iterable<Allocation> allocations) {
        this.allocations = ImmutableList.copyOf(allocations);
        this.allocationMap = new Byte[NUM_BINS];

        Map<String, Integer> treatments = Maps.newHashMap();
        int index = 0;
        for (Allocation allocation : allocations) {
            Integer treatment = treatments.get(allocation.getTreatment());
            if (treatment == null) {
                treatment = treatments.size();
                treatments.put(allocation.getTreatment(), treatment);
            }

            for (int i=0; i<allocation.getSize(); i++, index++) {
                this.allocationMap[index] = treatment.byteValue();
            }
        }

        this.size = index;
        this.treatmentsMap = new String[treatments.size()];
        for (Entry<String, Integer> entry : treatments.entrySet()) {
            this.treatmentsMap[entry.getValue()] = entry.getKey();
        }
    }

    public String getTreatment(int index) {
        Byte treatmentIndex = allocationMap[index];

        if (treatmentIndex == null) {
            // unallocated
            return null;
        }

        return treatmentsMap[treatmentIndex];
    }

    public List<Allocation> getAllocations() {
        return allocations;
    }

    public int getSize() {
        return size;
    }

    public int getUnallocatedSize() {
        return NUM_BINS - size;
    }
}
