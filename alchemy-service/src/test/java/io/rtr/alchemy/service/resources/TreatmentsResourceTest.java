package io.rtr.alchemy.service.resources;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import io.rtr.alchemy.dto.models.AllocationDto;
import io.rtr.alchemy.dto.models.TreatmentDto;
import io.rtr.alchemy.dto.requests.UpdateTreatmentRequest;
import org.junit.Test;

import javax.annotation.Nullable;
import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TreatmentsResourceTest extends ResourceTest {
    private static final String TREATMENTS_ENDPOINT = "/experiments/{experimentName}/treatments";
    private static final String TREATMENT_ENDPOINT = "/experiments/{experimentName}/treatments/{treatment}";
    private static final String ALLOCATIONS_ENDPOINT = "/experiments/{experimentName}/allocations";

    @Test
    public void testGetTreatments() {
        get(TREATMENTS_ENDPOINT, EXPERIMENT_BAD)
            .assertStatus(Status.NOT_FOUND);

        final Iterable<TreatmentDto> expected = MAPPER.toDto(experiment(EXPERIMENT_1).getTreatments(), TreatmentDto.class);
        final Iterable<TreatmentDto> actual =
            get(TREATMENTS_ENDPOINT, EXPERIMENT_1)
                .assertStatus(Status.OK)
                .result(iterable(TreatmentDto.class));

        assertEquals(expected, actual);
    }

    @Test
    public void testGetTreatment() {
        get(TREATMENT_ENDPOINT, EXPERIMENT_BAD, EXP_1_TREATMENT_1)
            .assertStatus(Status.NOT_FOUND);

        get(TREATMENT_ENDPOINT, EXPERIMENT_1, TREATMENT_BAD)
            .assertStatus(Status.NOT_FOUND);

        final TreatmentDto expected = MAPPER.toDto(experiment(EXPERIMENT_1).getTreatment(EXP_1_TREATMENT_1), TreatmentDto.class);
        final TreatmentDto actual =
            get(TREATMENT_ENDPOINT, EXPERIMENT_1, EXP_1_TREATMENT_1)
                .assertStatus(Status.OK)
                .result(TreatmentDto.class);

        assertEquals(expected, actual);
    }

    @Test
    public void testAddTreatment() {
        final TreatmentDto expected = new TreatmentDto("new_treatment", "this is a new treatment");

        put(TREATMENTS_ENDPOINT, EXPERIMENT_BAD)
            .entity(expected)
            .assertStatus(Status.NOT_FOUND);

        put(TREATMENTS_ENDPOINT, EXPERIMENT_1)
            .entity(expected)
            .assertStatus(Status.CREATED);

        final TreatmentDto actual = MAPPER.toDto(experiment(EXPERIMENT_1).getTreatment(expected.getName()), TreatmentDto.class);
        assertEquals(expected, actual);
    }

    @Test
    public void testRemoveTreamtment() {
        delete(TREATMENT_ENDPOINT, EXPERIMENT_BAD, EXP_1_TREATMENT_1)
            .assertStatus(Status.NOT_FOUND);

        delete(TREATMENT_ENDPOINT, EXPERIMENT_1, TREATMENT_BAD)
            .assertStatus(Status.NOT_FOUND);

        assertNotNull(experiment(EXPERIMENT_1).getTreatment(EXP_1_TREATMENT_1));

        delete(TREATMENT_ENDPOINT, EXPERIMENT_1, EXP_1_TREATMENT_1)
            .assertStatus(Status.NO_CONTENT);

        assertNull(experiment(EXPERIMENT_1).getTreatment(EXP_1_TREATMENT_1));
    }

    @Test
    public void testUpdateTreatment() {
        final UpdateTreatmentRequest request = new UpdateTreatmentRequest(Optional.of("new description"));

        post(TREATMENT_ENDPOINT, EXPERIMENT_BAD, EXP_1_TREATMENT_1)
            .entity(request)
            .assertStatus(Status.NOT_FOUND);

        post(TREATMENT_ENDPOINT, EXPERIMENT_1, TREATMENT_BAD)
            .entity(request)
            .assertStatus(Status.NOT_FOUND);

        post(TREATMENT_ENDPOINT, EXPERIMENT_1, EXP_1_TREATMENT_1)
            .entity(request)
            .assertStatus(Status.NO_CONTENT);

        final TreatmentDto treatment =
            get(TREATMENT_ENDPOINT, EXPERIMENT_1, EXP_1_TREATMENT_1)
                .assertStatus(Status.OK)
                .result(TreatmentDto.class);

        assertEquals(request.getDescription().orNull(), treatment.getDescription());

        final Iterable<AllocationDto> allocations =
            get(ALLOCATIONS_ENDPOINT, EXPERIMENT_1)
                .assertStatus(Status.OK)
                .result(iterable(AllocationDto.class));

        // Make sure we didn't lose our allocations for this treatment
        assertNotNull(
            Iterables.find(allocations, new Predicate<AllocationDto>() {
                @Override
                public boolean apply(@Nullable AllocationDto input) {
                    return input != null && input.getTreatment().equals(EXP_1_TREATMENT_1);
                }
            })
        );
    }

    @Test
    public void testClearTreatments() {
        delete(TREATMENTS_ENDPOINT, EXPERIMENT_BAD)
            .assertStatus(Status.NOT_FOUND);

        assertNotNull(experiment(EXPERIMENT_1).getTreatment(EXP_1_TREATMENT_1));
        assertNotNull(experiment(EXPERIMENT_1).getTreatment(EXP_1_TREATMENT_2));
        assertNotNull(experiment(EXPERIMENT_1).getTreatment(EXP_1_TREATMENT_3));
        delete(TREATMENTS_ENDPOINT, EXPERIMENT_1)
            .assertStatus(Status.NO_CONTENT);
        assertNull(experiment(EXPERIMENT_1).getTreatment(EXP_1_TREATMENT_1));
        assertNull(experiment(EXPERIMENT_1).getTreatment(EXP_1_TREATMENT_2));
        assertNull(experiment(EXPERIMENT_1).getTreatment(EXP_1_TREATMENT_3));
    }
}
