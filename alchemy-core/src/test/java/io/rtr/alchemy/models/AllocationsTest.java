package io.rtr.alchemy.models;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AllocationsTest {
    private final Treatment control = new Treatment("control", "This is the control");
    private final Treatment withLogin = new Treatment("with_login", "User logged in");
    private final Treatment withoutLogin = new Treatment("without_login", "User didn't log in");

    @Test
    public void testConstructWithEmptyList() {
        final Allocations allocations = new Allocations(Lists.<Allocation>newArrayList());
        assertEquals("unallocated size should be number of bins", Allocations.NUM_BINS, allocations.getUnallocatedSize());
        assertEquals("allocation size should be zero", 0, allocations.getSize());
        assertTrue("allocations should be empty", allocations.getAllocations().isEmpty());

        for (int i=0; i < Allocations.NUM_BINS; i++) {
            assertNull("unallocated treatment should return as null", allocations.getTreatment(i));
        }
    }

    @Test
    public void testConstructWithUnorderedAllocations() {
        final List<Allocation> allocationList = Lists.newArrayList(
            new Allocation(control, 0, 20),
            new Allocation(withoutLogin, 50, 40),
            new Allocation(withLogin, 20, 30)
        );

        final Allocations allocations = new Allocations(allocationList);

        assertEquals("unallocated size should match", Allocations.NUM_BINS - 90, allocations.getUnallocatedSize());
        assertEquals("size should match", 90, allocations.getSize());

        Collections.sort(allocationList, new Comparator<Allocation>() {
            @Override
            public int compare(Allocation lhs, Allocation rhs) {
                return Integer.compare(lhs.getOffset(), rhs.getOffset());
            }
        });
        assertEquals("allocations should match sorted original input", allocationList, allocations.getAllocations());

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

    @Test(expected = IllegalStateException.class)
    public void testConstructWithOverlappingAllocations() {
        final List<Allocation> allocationList = Lists.newArrayList(
            new Allocation(control, 0, 20),
            new Allocation(withoutLogin, 10, 20)
        );

        new Allocations(allocationList);
    }

    @Test(expected = IllegalStateException.class)
    public void testAllocatingTooMuch() {
        final Allocations allocations = new Allocations(Lists.<Allocation>newArrayList());
        allocations.allocate(control, Allocations.NUM_BINS + 1);
    }

    @Test
    public void testAllocate() {
        final Allocations allocations = new Allocations(Lists.<Allocation>newArrayList());

        // 0123456789|0123456789|0123456789|0123456789|0123456789|0123456789
        // AAAAAAAAAA|BBBBBBBBBB|BBBBBBBBBB|CCCCCCCCCC|CCCCCCCCCC|CCCCCCCCCC
        allocations.allocate(control, 10);
        allocations.allocate(withLogin, 20);
        allocations.allocate(withoutLogin, 30);

        assertEquals(
            "should be three contiguous allocations of various sizes",
            Lists.newArrayList(
                new Allocation(control, 0, 10),
                new Allocation(withLogin, 10, 20),
                new Allocation(withoutLogin, 30, 30)
            ),
            allocations.getAllocations()
        );
    }

    @Test
    public void testAllocateAdjacent() {
        final Allocations allocations = new Allocations(Lists.<Allocation>newArrayList());

        // 01234
        // AAAAA
        allocations.allocate(control, 5);

        // 0123456789
        // AAAAAAAAAA
        allocations.allocate(control, 5);

        assertEquals(
            "both allocations should have been merged into one allocation",
            Lists.newArrayList(
                new Allocation(control, 0, 10)
            ),
            allocations.getAllocations()
        );
    }

    @Test
    public void testDeallocate() {
        final Allocations allocations = new Allocations(Lists.<Allocation>newArrayList());

        // 0123456789|0123456789|0123456789|0123456789|0123456789|0123456789
        // AAAAAAAAAA|BBBBBBBBBB|BBBBBBBBBB|CCCCCCCCCC|CCCCCCCCCC|CCCCCCCCCC
        allocations.allocate(control, 10);
        allocations.allocate(withLogin, 20);
        allocations.allocate(withoutLogin, 30);

        // 0123456789|0123456789|0123456789|0123456789|0123456789|0123456789
        //      AAAAA|          |BBBBBBBBBB|          |     CCCCC|CCCCCCCCCC
        allocations.deallocate(control, 5);
        allocations.deallocate(withLogin, 10);
        allocations.deallocate(withoutLogin, 15);

        assertEquals(
            "allocations should be smaller and within the same offset range",
            // deallocations are applied from left to right
            Lists.newArrayList(
                new Allocation(control, 5, 5),
                new Allocation(withLogin, 20, 10),
                new Allocation(withoutLogin, 45, 15)
            ),
            allocations.getAllocations()
        );
    }

    @Test
    public void testDeallocateAllocateAdjacent() {
        final Allocations allocations = new Allocations(Lists.<Allocation>newArrayList());

        // 0123456789|0123456789
        // AAAAAAAAAA|BBBBBBBBBB
        allocations.allocate(control, 10);
        allocations.allocate(withLogin, 10);

        // 0123456789|0123456789
        // AAAAAAAAAA|     BBBBB
        allocations.deallocate(withLogin, 5);
        allocations.deallocate(withoutLogin, 15);

        // 0123456789|0123456789
        // AAAAAAAAAA|AAAAABBBBB
        allocations.allocate(control, 5);

        assertEquals(
            "allocation that filled the gap after deallocation should have been merged with adjacent allocation",
            Lists.newArrayList(
                new Allocation(control, 0, 15),
                new Allocation(withLogin, 15, 5)
            ),
            allocations.getAllocations()
        );
    }

    @Test
    public void testAllocateFillsGaps() {
        final Allocations allocations = new Allocations(Lists.<Allocation>newArrayList());

        // 0123456789
        // AA  BB  CC
        allocations.allocate(control, 2);
        allocations.allocate(withLogin, 4);
        allocations.allocate(withoutLogin, 4);
        allocations.deallocate(withLogin, 2);
        allocations.deallocate(withoutLogin, 2);

        // 0123456789
        // AAAABBAACC
        allocations.allocate(control, 4);

        assertEquals(
            "new allocation should have filled the gaps and merged with first allocation",
            Lists.newArrayList(
                new Allocation(control, 0, 4),
                new Allocation(withLogin, 4, 2),
                new Allocation(control, 6, 2),
                new Allocation(withoutLogin, 8, 2)
                ),
            allocations.getAllocations()
        );
    }

    @Test
    public void testReallocate() {
        final Allocations allocations = new Allocations(Lists.<Allocation>newArrayList());

        // 0123456789
        // AAAAABBBBB
        allocations.allocate(control, 5);
        allocations.allocate(withLogin, 5);

        // 0123456789
        // AAAAAAAABB
        allocations.reallocate(withLogin, control, 3);

        assertEquals(
            "reallocation should have partly replaced second allocation and merged with first allocation",
            Lists.newArrayList(
                new Allocation(control, 0, 8),
                new Allocation(withLogin, 8, 2)
            ),
            allocations.getAllocations()
        );
    }

    @Test
    public void testReallocateMultiple() {
        final Allocations allocations = new Allocations(Lists.<Allocation>newArrayList());

        // 0123456789
        // AABBAABBAA
        allocations.allocate(control, 2);
        allocations.allocate(withLogin, 2);
        allocations.allocate(control, 2);
        allocations.allocate(withLogin, 2);
        allocations.allocate(control, 2);

        // 0123456789
        // AAAAAAABAA
        allocations.reallocate(withLogin, control, 3);

        assertEquals(
            "reallocation should have replaced all occurrences of second allocation, except one bin",
            Lists.newArrayList(
                new Allocation(control, 0, 7),
                new Allocation(withLogin, 7, 1),
                new Allocation(control, 8, 2)
            ),
            allocations.getAllocations()
        );
    }

    @Test
    public void testClear() {
        final Treatment t1 = new Treatment("control");
        final Treatment t2 = new Treatment("other");
        final  Allocations allocations = new Allocations(Lists.newArrayList(
            new Allocation(t1, 0, 5),
            new Allocation(t2, 5, 5)
        ));

        assertEquals(2, allocations.getAllocations().size());
        assertEquals(10, allocations.getSize());

        allocations.clear();

        assertEquals(0, allocations.getAllocations().size());
        assertEquals(0, allocations.getSize());
    }
}
