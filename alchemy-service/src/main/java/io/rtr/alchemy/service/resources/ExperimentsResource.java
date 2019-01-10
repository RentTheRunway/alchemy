package io.rtr.alchemy.service.resources;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import io.rtr.alchemy.db.Filter;
import io.rtr.alchemy.db.Ordering;
import io.rtr.alchemy.dto.models.ExperimentDto;
import io.rtr.alchemy.dto.models.TreatmentDto;
import io.rtr.alchemy.dto.requests.AllocateRequest;
import io.rtr.alchemy.dto.requests.CreateExperimentRequest;
import io.rtr.alchemy.dto.requests.TreatmentOverrideRequest;
import io.rtr.alchemy.dto.requests.UpdateExperimentRequest;
import io.rtr.alchemy.filtering.FilterExpression;
import io.rtr.alchemy.mapping.Mappers;
import io.rtr.alchemy.models.Experiment;
import io.rtr.alchemy.models.Experiments;
import io.rtr.alchemy.models.Treatment;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

/**
 * Resource for interacting with experiments
 */
@Path("/experiments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ExperimentsResource extends BaseResource {
    private final Experiments experiments;
    private final Mappers mapper;

    @Inject
    public ExperimentsResource(Experiments experiments, Mappers mapper) {
        this.experiments = experiments;
        this.mapper = mapper;
    }

    @GET
    public Iterable<ExperimentDto> getExperiments(@QueryParam("filter") String filterValue,
                                                  @QueryParam("offset") Integer offset,
                                                  @QueryParam("limit") Integer limit,
                                                  @QueryParam("sort") String sort) {
        return mapper.toDto(
            experiments.find(
                    Filter
                        .criteria()
                        .filter(filterValue)
                        .offset(offset)
                        .limit(limit)
                        .ordering(Ordering.parse(sort))
                        .build()
            ),
            ExperimentDto.class
        );
    }

    @PUT
    public Response addExperiment(@Valid CreateExperimentRequest request) {
        final Experiment experiment =
            experiments
                .create(request.getName())
                .setDescription(request.getDescription());

        if (request.getFilter() != null) {
            experiment.setFilter(FilterExpression.of(request.getFilter()));
        }

        if (request.getHashAttributes() != null) {
            experiment.setHashAttributes(request.getHashAttributes());
        }

        if (request.getSeed() != null) {
            experiment.setSeed(request.getSeed());
        }

        if (request.getTreatments() != null) {
            for (final TreatmentDto treatment : request.getTreatments()) {
                experiment.addTreatment(treatment.getName(), treatment.getDescription());
            }
        }

        if (request.getAllocations() != null) {
            for (final AllocateRequest allocation : request.getAllocations()) {
                experiment.allocate(allocation.getTreatment(), allocation.getSize());
            }
        }

        if (request.getOverrides() != null) {
            for (final TreatmentOverrideRequest override : request.getOverrides()) {
                experiment.addOverride(override.getName(), override.getTreatment(), override.getFilter());
            }
        }

        if (request.isActive() != null) {
            if (request.isActive()) {
                experiment.activate();
            } else {
                experiment.deactivate();
            }
        }

        experiment.save();

        return created();
    }

    @POST
    @Path("/{experimentName}")
    public void updateExperiment(@PathParam("experimentName") String experimentName,
                                 @Valid UpdateExperimentRequest request) {
        final Experiment experiment = ensureExists(experiments.get(experimentName));

        if (request.getSeed() != null && request.getSeed().isPresent()) {
            experiment.setSeed(request.getSeed().or(0));
        }

        if (request.getDescription() != null) {
            experiment.setDescription(request.getDescription().orNull());
        }

        if (request.getFilter() != null && request.getFilter().isPresent()) {
            experiment.setFilter(FilterExpression.of(request.getFilter().orNull()));
        }

        if (request.getHashAttributes() != null && request.getHashAttributes().isPresent()) {
            experiment.setHashAttributes(request.getHashAttributes().orNull());
        }

        // only remove treatments not present in request, otherwise we wipe out existing allocations
        if (request.getTreatments() != null) {
            final Set<Integer> missingTreatments = Sets.newHashSet();
            for (final Treatment treatment : experiment.getTreatments()) {
                missingTreatments.add(treatment.getName());
            }

            if (request.getTreatments().isPresent()) {
                for (final TreatmentDto treatment : request.getTreatments().get()) {
                    missingTreatments.remove(treatment.getName());
                    final Treatment existingTreatment = experiment.getTreatment(treatment.getName());

                    if (existingTreatment != null) {
                        existingTreatment.setDescription(treatment.getDescription());
                    } else {
                        experiment.addTreatment(treatment.getName(), treatment.getDescription());
                    }
                }
            }

            for (final Integer missingTreatment : missingTreatments) {
                experiment.removeTreatment(missingTreatment);
            }
        }

        if (request.getAllocations() != null) {
            experiment.deallocateAll();

            if (request.getAllocations().isPresent()) {
                for (final AllocateRequest allocation : request.getAllocations().get()) {
                    experiment.allocate(allocation.getTreatment(), allocation.getSize());
                }
            }
        }

        if (request.getOverrides() != null) {
            experiment.clearOverrides();

            if (request.getOverrides().isPresent()) {
                for (final TreatmentOverrideRequest override : request.getOverrides().get()) {
                    experiment.addOverride(
                        override.getName(),
                        override.getTreatment(),
                        override.getFilter()
                    );
                }
            }
        }

        if (request.getActive() != null && request.getActive().isPresent()) {
            if (request.getActive().get()) {
                experiment.activate();
            } else {
                experiment.deactivate();
            }
        }

        experiment.save();
    }

    @GET
    @Path("/{experimentName}")
    public ExperimentDto getExperiment(@PathParam("experimentName") String experimentName) {
        return mapper.toDto(
            ensureExists(experiments.get(experimentName)),
            ExperimentDto.class
        );
    }

    @DELETE
    @Path("/{experimentName}")
    public void removeExperiment(@PathParam("experimentName") String experimentName) {
        ensureExists(experiments.get(experimentName))
            .delete();
    }
}
