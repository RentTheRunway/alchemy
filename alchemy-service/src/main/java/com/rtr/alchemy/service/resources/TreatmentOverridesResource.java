package com.rtr.alchemy.service.resources;

import com.google.inject.Inject;
import com.rtr.alchemy.dto.models.TreatmentOverrideDto;
import com.rtr.alchemy.dto.requests.TreatmentOverrideRequest;
import com.rtr.alchemy.identities.Identity;
import com.rtr.alchemy.mapping.Mappers;
import com.rtr.alchemy.models.Experiment;
import com.rtr.alchemy.models.Experiments;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Resource for interacting with treatment overrides
 */
@Path("/experiments/{experimentName}/overrides")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TreatmentOverridesResource extends BaseResource {
    private final Experiments experiments;
    private final Mappers mapper;

    @Inject
    public TreatmentOverridesResource(Experiments experiments, Mappers mapper) {
        this.experiments = experiments;
        this.mapper = mapper;
    }

    @GET
    @Path("/{overrideName}")
    public TreatmentOverrideDto getOverride(@PathParam("experimentName") String experimentName,
                                            @PathParam("overrideName") String overrideName) {
        return mapper.toDto(
            ensureExists(
                ensureExists(
                    experiments.get(experimentName)
                ).getOverride(overrideName)
            ),
            TreatmentOverrideDto.class
        );
    }

    @GET
    public Iterable<TreatmentOverrideDto> getOverrides(@PathParam("experimentName") String experimentName) {
        return mapper.toDto(
            ensureExists(experiments.get(experimentName)).getOverrides(),
            TreatmentOverrideDto.class
        );
    }

    @PUT
    public Response addOverride(@PathParam("experimentName") String experimentName,
                                @Valid TreatmentOverrideRequest request) {
        ensureExists(experiments.get(experimentName))
            .addOverride(
                request.getName(),
                request.getTreatment(),
                mapper.fromDto(request.getIdentity(), Identity.class)
            )
            .save();

        return created();
    }

    @DELETE
    public void clearOverrides(@PathParam("experimentName") String experimentName) {
        ensureExists(experiments.get(experimentName))
            .clearOverrides()
            .save();
    }

    @DELETE
    @Path("/{overrideName}")
    public void removeOverride(@PathParam("experimentName") String experimentName,
                               @PathParam("overrideName") String overrideName) {
        final Experiment experiment = ensureExists(experiments.get(experimentName));
        ensureExists(experiment.getOverride(overrideName));
        experiment
            .removeOverride(overrideName)
            .save();
    }
}
