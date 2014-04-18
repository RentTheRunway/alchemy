package com.rtr.alchemy.service.resources;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.rtr.alchemy.dto.models.AllocationDto;
import com.rtr.alchemy.dto.requests.AllocationRequest;
import com.rtr.alchemy.dto.requests.AllocationRequests;
import org.junit.Test;

import javax.ws.rs.core.Response.Status;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AllocationsResourceTest extends ResourceTest {
    private static final String ALLOCATIONS_ENDPOINT = "/experiments/{experimentName}/allocations";

    private static Map<String, Integer> countAllocations(Iterable<AllocationDto> allocations) {
        final Map<String, Integer> result = Maps.newHashMap();

        for (AllocationDto allocation : allocations) {
            final Integer value = result.get(allocation.getTreatment());
            if (value == null) {
                result.put(allocation.getTreatment(), allocation.getSize());
            } else {
                result.put(allocation.getTreatment(), allocation.getSize() + value);
            }
        }

        return result;
    }

    private AllocationRequest.Allocate allocate(final String treatment, final int size) {
        return new AllocationRequest.Allocate() {
            @Override
            public String getTreatment() {
                return treatment;
            }

            @Override
            public Integer getSize() {
                return size;
            }
        };
    }

    private AllocationRequest.Deallocate deallocate(final String treatment, final int size) {
        return new AllocationRequest.Deallocate() {
            @Override
            public String getTreatment() {
                return treatment;
            }

            @Override
            public Integer getSize() {
                return size;
            }
        };
    }

    private AllocationRequest.Reallocate reallocate(final String treatment, final String target, final int size) {
        return new AllocationRequest.Reallocate() {
            @Override
            public String getTarget() {
                return target;
            }

            @Override
            public String getTreatment() {
                return treatment;
            }

            @Override
            public Integer getSize() {
                return size;
            }
        };
    }

    @Test
    public void testGetAllocations() {
        get(ALLOCATIONS_ENDPOINT, EXPERIMENT_BAD)
            .assertStatus(Status.NOT_FOUND);

        final Iterable<AllocationDto> expected = MAPPER.toDto(experiment(EXPERIMENT_1).getAllocations(), AllocationDto.class);
        final Iterable<AllocationDto> actual =
            get(ALLOCATIONS_ENDPOINT, EXPERIMENT_1)
                .assertStatus(Status.OK)
                .result(iterable(AllocationDto.class));

        assertEquals(expected, actual);
    }

    @Test
    public void testUpdateAllocations() {
        final AllocationRequests allocationRequest = AllocationRequests.of(
            deallocate(EXP_1_TREATMENT_2, 10),
            allocate(EXP_1_TREATMENT_1, 5),
            reallocate(EXP_1_TREATMENT_3, EXP_1_TREATMENT_1, 15)
        );

        post(ALLOCATIONS_ENDPOINT, EXPERIMENT_BAD)
            .entity(allocationRequest)
            .assertStatus(Status.NOT_FOUND);

        final Map<String, Integer> allocations = ImmutableMap.of(
            EXP_1_TREATMENT_1, 50,
            EXP_1_TREATMENT_2, 25,
            EXP_1_TREATMENT_3, 25
        );

        final Iterable<AllocationDto> currentAllocations = MAPPER.toDto(
            experiment(EXPERIMENT_1).getAllocations(),
            AllocationDto.class
        );

        assertEquals(allocations, countAllocations(currentAllocations));

        post(ALLOCATIONS_ENDPOINT, EXPERIMENT_1)
            .entity(allocationRequest)
            .assertStatus(Status.NO_CONTENT);

        final Map<String, Integer> expectedAllocations = ImmutableMap.of(
            EXP_1_TREATMENT_1, 75,
            EXP_1_TREATMENT_2, 15,
            EXP_1_TREATMENT_3, 10
        );

        final Iterable<AllocationDto> newAllocations = MAPPER.toDto(
            experiment(EXPERIMENT_1).getAllocations(),
            AllocationDto.class
        );

        assertEquals(expectedAllocations, countAllocations(newAllocations));
    }

    @Test
    public void testClearAllocations() {
        delete(ALLOCATIONS_ENDPOINT, EXPERIMENT_BAD).assertStatus(Status.NOT_FOUND);
        delete(ALLOCATIONS_ENDPOINT, EXPERIMENT_1).assertStatus(Status.NO_CONTENT);

        assertTrue(experiment(EXPERIMENT_1).getAllocations().isEmpty());
    }
}
