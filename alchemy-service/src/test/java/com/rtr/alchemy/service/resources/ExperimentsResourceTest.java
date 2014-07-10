package com.rtr.alchemy.service.resources;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.rtr.alchemy.dto.models.ExperimentDto;
import com.rtr.alchemy.dto.models.TreatmentDto;
import com.rtr.alchemy.dto.requests.AllocateRequest;
import com.rtr.alchemy.dto.requests.CreateExperimentRequest;
import com.rtr.alchemy.dto.requests.TreatmentOverrideRequest;
import com.rtr.alchemy.dto.requests.UpdateExperimentRequest;
import com.rtr.alchemy.filtering.FilterExpression;
import org.junit.Test;

import javax.ws.rs.core.Response.Status;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ExperimentsResourceTest extends ResourceTest {
    private static final String EXPERIMENTS_ENDPOINT = "/experiments";
    private static final String EXPERIMENT_ENDPOINT = "/experiments/{experimentName}";

    @Test
    public void testGetExperiments() {
        final Iterable<ExperimentDto> expected = MAPPER.toDto(EXPERIMENTS.find(), ExperimentDto.class);
        final Iterable<ExperimentDto> actual =
            get(EXPERIMENTS_ENDPOINT)
                .assertStatus(Status.OK)
                .result(iterable(ExperimentDto.class));
        assertEquals(expected, actual);
    }

    @Test
    public void testGetExperiment() {
        get(EXPERIMENT_ENDPOINT, EXPERIMENT_BAD)
            .assertStatus(Status.NOT_FOUND);

        final ExperimentDto expected = MAPPER.toDto(experiment(EXPERIMENT_1), ExperimentDto.class);
        final ExperimentDto actual =
            get(EXPERIMENT_ENDPOINT, EXPERIMENT_1)
                .assertStatus(Status.OK)
                .result(ExperimentDto.class);

        assertEquals(expected, actual);
    }

    @Test
    public void testAddExperiment() {
        final CreateExperimentRequest request =
            new CreateExperimentRequest(
                "new_experiment",
                0,
                "it's new",
                null,
                null,
                false,
                Lists.newArrayList(
                    new TreatmentDto("control", "the default"),
                    new TreatmentDto("first_case", "the first case"),
                    new TreatmentDto("second_case", "the second case")
                ),
                Lists.newArrayList(
                    new AllocateRequest("control", 10),
                    new AllocateRequest("first_case", 20),
                    new AllocateRequest("second_case", 30)
                ),
                Lists.<TreatmentOverrideRequest>newArrayList()
            );

        assertNull(experiment("new_experiment"));
        put(EXPERIMENTS_ENDPOINT)
            .entity(request)
            .assertStatus(Status.CREATED);
        assertNotNull(experiment("new_experiment"));
    }

    @Test
    public void testUpdateExperiment() {
        final UpdateExperimentRequest request =
            new UpdateExperimentRequest(
                Optional.<Integer>absent(),
                Optional.of("new description"),
                Optional.of("device"),
                Optional.<LinkedHashSet<String>>absent(),
                Optional.of(false),
                Optional.<List<TreatmentDto>>absent(),
                Optional.<List<AllocateRequest>>absent(),
                Optional.<List<TreatmentOverrideRequest>>absent()
            );

        final ExperimentDto expected = MAPPER.toDto(
                experiment(EXPERIMENT_1)
                    .setDescription(request.getDescription().get())
                    .setFilter(FilterExpression.of(request.getFilter().get()))
                    .deactivate(),
                ExperimentDto.class
        );

        post(EXPERIMENT_ENDPOINT, EXPERIMENT_BAD)
            .entity(request)
            .assertStatus(Status.NOT_FOUND);


        post(EXPERIMENT_ENDPOINT, EXPERIMENT_1)
            .entity(request)
            .assertStatus(Status.NO_CONTENT);

        final ExperimentDto actual = MAPPER.toDto(experiment(EXPERIMENT_1), ExperimentDto.class);
        assertEquals(expected, actual);
    }

    @Test
    public void testRemoveExperiment() {
        delete(EXPERIMENT_ENDPOINT, EXPERIMENT_BAD)
            .assertStatus(Status.NOT_FOUND);

        assertNotNull(EXPERIMENTS.get(EXPERIMENT_1));
        delete(EXPERIMENT_ENDPOINT, EXPERIMENT_1)
            .assertStatus(Status.NO_CONTENT);
        assertNull(EXPERIMENTS.get(EXPERIMENT_1));
    }
}
