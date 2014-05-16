package com.rtr.alchemy.service.resources;

import com.rtr.alchemy.dto.models.TreatmentOverrideDto;
import com.rtr.alchemy.dto.requests.TreatmentOverrideRequest;
import org.junit.Test;

import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TreatmentOverridesResourceTest extends ResourceTest {
    private static final String ENDPOINT_OVERRIDES = "/experiments/{experimentName}/overrides";
    private static final String ENDPOINT_OVERRIDE = "/experiments/{experimentName}/overrides/{overrideName}";

    @Test
    public void testGetOverrides() {
        get(ENDPOINT_OVERRIDES, EXPERIMENT_BAD)
            .assertStatus(Status.NOT_FOUND);

        final Iterable<TreatmentOverrideDto> expected = MAPPER.toDto(
            experiment(EXPERIMENT_1).getOverrides(),
            TreatmentOverrideDto.class
        );

        final Iterable<TreatmentOverrideDto> actual =
            get(ENDPOINT_OVERRIDES, EXPERIMENT_1)
                .assertStatus(Status.OK)
                .result(iterable(TreatmentOverrideDto.class));

        assertEquals(expected, actual);
    }

    @Test
    public void testGetOverride() {
        get(ENDPOINT_OVERRIDE, EXPERIMENT_BAD, EXP_1_OVERRIDE)
            .assertStatus(Status.NOT_FOUND);

        get(ENDPOINT_OVERRIDE, EXPERIMENT_1, OVERRIDE_BAD)
            .assertStatus(Status.NOT_FOUND);

        final TreatmentOverrideDto expected = MAPPER.toDto(
            experiment(EXPERIMENT_1).getOverride(EXP_1_OVERRIDE),
            TreatmentOverrideDto.class
        );

        final TreatmentOverrideDto actual =
            get(ENDPOINT_OVERRIDE, EXPERIMENT_1, EXP_1_OVERRIDE)
                .assertStatus(Status.OK)
                .result(TreatmentOverrideDto.class);

        assertEquals(expected, actual);
    }

    @Test
    public void testAddOverride() {
        final TreatmentOverrideRequest request = new TreatmentOverrideRequest("control", new UserDto("qa"), "qa_control");

        put(ENDPOINT_OVERRIDES, EXPERIMENT_BAD)
            .entity(request)
            .assertStatus(Status.NOT_FOUND);

        final TreatmentOverrideDto expected = new TreatmentOverrideDto(request.getName(), request.getTreatment());

        put(ENDPOINT_OVERRIDES, EXPERIMENT_1)
            .entity(request)
            .assertStatus(Status.CREATED);

        final TreatmentOverrideDto actual = MAPPER.toDto(
            experiment(EXPERIMENT_1).getOverride(request.getName()),
            TreatmentOverrideDto.class
        );

        assertEquals(expected, actual);
    }

    @Test
    public void testRemoveOverride() {
        delete(ENDPOINT_OVERRIDE, EXPERIMENT_BAD, EXP_1_OVERRIDE)
            .assertStatus(Status.NOT_FOUND);

        delete(ENDPOINT_OVERRIDE, EXPERIMENT_1, OVERRIDE_BAD)
            .assertStatus(Status.NOT_FOUND);

        assertNotNull(experiment(EXPERIMENT_1).getOverride(EXP_1_OVERRIDE));
        delete(ENDPOINT_OVERRIDE, EXPERIMENT_1, EXP_1_OVERRIDE)
            .assertStatus(Status.NO_CONTENT);
        assertNull(experiment(EXPERIMENT_1).getOverride(EXP_1_OVERRIDE));
    }

    @Test
    public void testClearOverrides() {
        delete(ENDPOINT_OVERRIDES, EXPERIMENT_BAD)
            .assertStatus(Status.NOT_FOUND);

        assertNotNull(experiment(EXPERIMENT_1).getOverride(EXP_1_OVERRIDE));
        delete(ENDPOINT_OVERRIDES, EXPERIMENT_1)
            .assertStatus(Status.NO_CONTENT);
        assertNull(experiment(EXPERIMENT_1).getOverride(EXP_1_OVERRIDE));
    }
}
