package io.rtr.alchemy.service.resources;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableMap;

import io.rtr.alchemy.dto.models.TreatmentDto;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import javax.ws.rs.core.Response.Status;

public class ActiveTreatmentsResourceTest extends ResourceTest {
    private static final String ENDPOINT_ACTIVE_TREATMENT =
            "/active/experiments/{experimentName}/treatment";
    private static final String ENDPOINT_ACTIVE_TREATMENTS = "/active/treatments";
    private UserDto userDto;
    private User user;
    private DeviceDto deviceDto;
    private Device device;

    @Before
    public void setUp() {
        super.setUp();

        userDto = new UserDto("user");
        user = MAPPER.fromDto(userDto, User.class);

        deviceDto = new DeviceDto("0a1b2c3d4fdeadbeef");
        device = MAPPER.fromDto(deviceDto, Device.class);
    }

    @Test
    public void testGetActiveTreatment() {
        post(ENDPOINT_ACTIVE_TREATMENT, EXPERIMENT_BAD)
                .entity(userDto)
                .assertStatus(Status.NO_CONTENT);

        final TreatmentDto expected1 =
                MAPPER.toDto(
                        experiment(EXPERIMENT_1).getTreatment(user, user.computeAttributes()),
                        TreatmentDto.class);
        final TreatmentDto expected2 =
                MAPPER.toDto(
                        experiment(EXPERIMENT_2).getTreatment(device, device.computeAttributes()),
                        TreatmentDto.class);

        final TreatmentDto actual1 =
                post(ENDPOINT_ACTIVE_TREATMENT, EXPERIMENT_1)
                        .entity(userDto)
                        .assertStatus(Status.OK)
                        .result(TreatmentDto.class);

        assertEquals(expected1, actual1);

        // wrong type
        post(ENDPOINT_ACTIVE_TREATMENT, EXPERIMENT_1)
                .entity(deviceDto)
                .assertStatus(Status.NO_CONTENT);

        final TreatmentDto actual2 =
                post(ENDPOINT_ACTIVE_TREATMENT, EXPERIMENT_2)
                        .entity(deviceDto)
                        .assertStatus(Status.OK)
                        .result(TreatmentDto.class);

        assertEquals(expected2, actual2);

        // not active
        post(ENDPOINT_ACTIVE_TREATMENT, EXPERIMENT_3)
                .entity(userDto)
                .assertStatus(Status.NO_CONTENT);

        // not allocated
        post(ENDPOINT_ACTIVE_TREATMENT, EXPERIMENT_4)
                .entity(userDto)
                .assertStatus(Status.NO_CONTENT);
    }

    @Test
    public void testGetActiveTreatments() {
        final Map<String, TreatmentDto> expected =
                ImmutableMap.of(
                        EXPERIMENT_1,
                        MAPPER.toDto(
                                experiment(EXPERIMENT_1)
                                        .getTreatment(user, user.computeAttributes()),
                                TreatmentDto.class));

        final Map<String, TreatmentDto> actual =
                post(ENDPOINT_ACTIVE_TREATMENTS)
                        .entity(userDto)
                        .assertStatus(Status.OK)
                        .result(map(String.class, TreatmentDto.class));

        assertEquals(expected, actual);
    }
}
