package com.rtr.alchemy.service.resources;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.rtr.alchemy.dto.models.ExperimentDto;
import com.rtr.alchemy.dto.models.TreatmentDto;
import com.rtr.alchemy.dto.requests.AllocateRequest;
import com.rtr.alchemy.dto.requests.CreateExperimentRequest;
import com.rtr.alchemy.dto.requests.TreatmentOverrideRequest;
import com.rtr.alchemy.dto.requests.UpdateExperimentRequest;
import org.junit.Test;

import javax.ws.rs.core.Response.Status;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ExperimentsResourceTest extends ResourceTest {
    private static final String EXPERIMENTS_ENDPOINT = "/experiments";
    private static final String EXPERIMENT_ENDPOINT = "/experiments/{experimentName}";

    private AllocateRequest allocate(final String treatment, final int size) {
        return new AllocateRequest() {
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
        final CreateExperimentRequest request = new CreateExperimentRequest() {
            @Override
            public String getName() {
                return "new_experiment";
            }

            @Override
            public String getDescription() {
                return "it's new";
            }

            @Override
            public String getIdentityType() {
                return null;
            }

            @Override
            public Boolean isActive() {
                return false;
            }

            @Override
            public List<TreatmentDto> getTreatments() {
                return Lists.newArrayList(
                    new TreatmentDto("control", "the default"),
                    new TreatmentDto("first_case", "the first case"),
                    new TreatmentDto("second_case", "the second case")
                );
            }

            @Override
            public List<AllocateRequest> getAllocations() {
                return Lists.newArrayList(
                    allocate("control", 10),
                    allocate("first_case", 20),
                    allocate("second_case", 30)
                );
            }

            @Override
            public List<TreatmentOverrideRequest> getOverrides() {
                return Lists.newArrayList();
            }
        };

        assertNull(experiment("new_experiment"));
        put(EXPERIMENTS_ENDPOINT)
            .entity(request)
            .assertStatus(Status.CREATED);
        assertNotNull(experiment("new_experiment"));
    }

    @Test
    public void testUpdateExperiment() {
        final UpdateExperimentRequest request = new UpdateExperimentRequest() {
            @Override
            public Optional<String> getDescription() {
                return Optional.of("new description");
            }

            @Override
            public Optional<String> getIdentityType() {
                return Optional.of("device");
            }

            @Override
            public Optional<Boolean> getActive() {
                return Optional.of(false);
            }

            @Override
            public Optional<List<TreatmentDto>> getTreatments() {
                return Optional.absent();
            }

            @Override
            public Optional<List<AllocateRequest>> getAllocations() {
                return Optional.absent();
            }

            @Override
            public Optional<List<TreatmentOverrideRequest>> getOverrides() {
                return Optional.absent();
            }
        };

        final ExperimentDto expected = MAPPER.toDto(
            experiment(EXPERIMENT_1)
                .setDescription(request.getDescription().get())
                .setIdentityType(request.getIdentityType().get())
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
