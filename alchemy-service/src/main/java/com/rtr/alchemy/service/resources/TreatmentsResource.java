package com.rtr.alchemy.service.resources;

import com.google.inject.Inject;
import com.rtr.alchemy.dto.models.TreatmentDto;
import com.rtr.alchemy.mapping.Mappers;
import com.rtr.alchemy.models.Experiment;
import com.rtr.alchemy.models.Experiments;
import com.rtr.alchemy.models.Treatment;

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
 * Resource for interacting with treatments
 */
@Path("/experiments/{experimentName}/treatments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TreatmentsResource extends BaseResource {
    private final Experiments experiments;
    private final Mappers mapper;

    @Inject
    public TreatmentsResource(Experiments experiments, Mappers mapper) {
        this.experiments = experiments;
        this.mapper = mapper;
    }

    @GET
    public Iterable<TreatmentDto> getTreatments(@PathParam("experimentName") String experimentName) {
        return mapper.toDto(
            ensureExists(experiments.get(experimentName)).getTreatments(),
            TreatmentDto.class
        );
    }

    @GET
    @Path("/{treatmentName}")
    public TreatmentDto getTreatment(@PathParam("experimentName") String experimentName,
                                     @PathParam("treatmentName") String treatmentName) {
        return mapper.toDto(
            ensureExists(
                ensureExists(experiments.get(experimentName)).getTreatment(treatmentName)
            ),
            TreatmentDto.class
        );
    }

    @PUT
    public Response addTreatment(@PathParam("experimentName") String experimentName,
                                 @Valid TreatmentDto treatmentDto) {
        ensureExists(experiments.get(experimentName))
            .addTreatment(treatmentDto.getName(), treatmentDto.getDescription())
            .save();

        return created();
    }

    @DELETE
    @Path("/{treatmentName}")
    public void removeTreatment(@PathParam("experimentName") String experimentName,
                                @PathParam("treatmentName") String treatmentName) {
        final Experiment experiment = ensureExists(experiments.get(experimentName));
        ensureExists(experiment.getTreatment(treatmentName));

        experiment
            .removeTreatment(treatmentName)
            .save();
    }

    @DELETE
    public void clearTreatments(@PathParam("experimentName") String experimentName) {
        ensureExists(experiments.get(experimentName))
            .clearTreatments()
            .save();
    }
}
