package io.rtr.alchemy.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/** Represents allocations of treatments for an experiment */
public class Allocations {
    public static final int NUM_BINS = 100;
    private static final Comparator<Allocation> COMPARATOR =
            Comparator.comparingInt(Allocation::getOffset);

    private Byte[] allocationMap;
    private List<Allocation> allocations;
    private Treatment[] treatmentsMap;
    private int size;

    public Allocations() {
        this(new ArrayList<>());
    }

    public Allocations(final Iterable<Allocation> allocations) {
        this.allocations =
                StreamSupport.stream(allocations.spliterator(), false).collect(Collectors.toList());
        sortAllocations();
        rebuildAllocationTables();
    }

    private void sortAllocations() {
        allocations.sort(COMPARATOR);
    }

    private void rebuildAllocationTables() {
        final Map<Treatment, Integer> treatments = new HashMap<>();
        int size = 0;
        allocationMap = new Byte[NUM_BINS];

        for (final Allocation allocation : allocations) {
            final Integer treatment =
                    treatments.computeIfAbsent(allocation.getTreatment(), k -> treatments.size());

            for (int i = allocation.getOffset();
                    i < allocation.getOffset() + allocation.getSize();
                    i++, size++) {
                if (allocationMap[i] != null) {
                    throw new IllegalStateException("overlapping allocations");
                }
                allocationMap[i] = treatment.byteValue();
            }
        }

        this.size = size;
        this.treatmentsMap = new Treatment[treatments.size()];
        for (final Entry<Treatment, Integer> entry : treatments.entrySet()) {
            this.treatmentsMap[entry.getValue()] = entry.getKey();
        }
    }

    /**
     * Get treatment assigned to a specific bin
     *
     * @param bin The bin
     */
    public Treatment getTreatment(final int bin) {
        final Byte treatmentIndex = allocationMap[bin];

        if (treatmentIndex == null) {
            // unallocated
            return null;
        }

        return treatmentsMap[treatmentIndex];
    }

    private void mergeAdjacentAllocations() {
        // merge all adjacent allocations for the same treatment
        if (allocations.size() < 2) {
            return;
        }

        final List<Allocation> newList = new ArrayList<>();
        int totalSize = allocations.get(0).getSize();
        int offset = allocations.get(0).getOffset();
        Treatment treatment = allocations.get(0).getTreatment();

        for (int i = 1; i < allocations.size(); i++) {
            final Allocation allocation = allocations.get(i);
            if (!allocation.getTreatment().equals(treatment)) {
                // different treatment
                newList.add(new Allocation(treatment, offset, totalSize));
                totalSize = allocation.getSize();
                offset = allocation.getOffset();
                treatment = allocation.getTreatment();
            } else {
                // same treatment, accumulate size
                totalSize += allocation.getSize();
            }
        }

        newList.add(new Allocation(treatment, offset, totalSize));
        allocations = newList;
    }

    /**
     * Allocates bins to a treatment
     *
     * @param treatment The treatment
     * @param size The number of bins
     */
    public void allocate(final Treatment treatment, final int size) {
        if (getUnallocatedSize() < size) {
            throw new IllegalArgumentException(
                    String.format(
                            "not enough free bins to allocate treatment %s with size %s given %s unallocated bin(s)",
                            treatment.getName(), size, getUnallocatedSize()));
        }

        // turn contiguous unallocated blocks into allocated blocks
        final List<Allocation> pieces = new ArrayList<>();
        int sizeLeft = size;
        int offset = 0;
        int index = 0;

        while (sizeLeft > 0) {
            if (index < allocations.size()) {
                final Allocation allocation = allocations.get(index);
                final int pieceSize = allocation.getOffset() - offset;
                if (pieceSize > 0) {
                    sizeLeft -= pieceSize;
                    pieces.add(new Allocation(treatment, offset, pieceSize));
                }
                offset = allocation.getOffset() + allocation.getSize();
                index++;
            } else {
                pieces.add(new Allocation(treatment, offset, sizeLeft));
                sizeLeft = 0;
            }
        }

        allocations.addAll(pieces);
        sortAllocations();
        mergeAdjacentAllocations();
        rebuildAllocationTables();
    }

    /**
     * De-allocates bins from a treatment
     *
     * @param treatment The treatment
     * @param size The number of bins
     */
    public void deallocate(final Treatment treatment, final int size) {
        final Iterator<Allocation> iter = allocations.iterator();
        int sizeLeft = size;

        while (iter.hasNext()) {
            final Allocation next = iter.next();

            if (next.getTreatment().equals(treatment)) {
                sizeLeft -= next.getSize();
                iter.remove();
            }

            if (sizeLeft < 0) {
                // a piece could not be evenly split by the amount we wanted to deallocate, need to
                // add back in
                allocations.add(
                        new Allocation(
                                treatment,
                                next.getOffset() + next.getSize() + sizeLeft,
                                -sizeLeft));
                break;
            }
        }

        sortAllocations();
        mergeAdjacentAllocations();
        rebuildAllocationTables();
    }

    /**
     * Reallocates bins from one treatment to another
     *
     * @param source The source treatment
     * @param destination The destination treatment
     * @param size The number of bins
     */
    public void reallocate(final Treatment source, final Treatment destination, final int size) {
        final Iterator<Allocation> iter = allocations.iterator();
        final List<Allocation> pieces = new ArrayList<>();
        int sizeLeft = size;
        int lastOffset = 0;

        while (iter.hasNext() && sizeLeft > 0) {
            final Allocation next = iter.next();
            lastOffset = next.getOffset() + next.getSize();

            if (!next.getTreatment().equals(source)) {
                continue;
            }

            sizeLeft -= next.getSize();
            iter.remove();

            if (sizeLeft < 0) {
                // a piece could not be evenly split by the amount we wanted to deallocate, need to
                // add back in
                pieces.add(
                        new Allocation(destination, next.getOffset(), next.getSize() + sizeLeft));
                pieces.add(
                        new Allocation(
                                source, next.getOffset() + next.getSize() + sizeLeft, -sizeLeft));
            } else {
                pieces.add(new Allocation(destination, next.getOffset(), next.getSize()));
            }
        }

        if (sizeLeft > 0) {
            pieces.add(new Allocation(destination, lastOffset, sizeLeft));
        }

        allocations.addAll(pieces);

        sortAllocations();
        mergeAdjacentAllocations();
        rebuildAllocationTables();
    }

    public List<Allocation> getAllocations() {
        return List.copyOf(allocations);
    }

    public int getSize() {
        return size;
    }

    public int getUnallocatedSize() {
        return NUM_BINS - size;
    }

    public void clear() {
        allocations.clear();
        rebuildAllocationTables();
    }
}
