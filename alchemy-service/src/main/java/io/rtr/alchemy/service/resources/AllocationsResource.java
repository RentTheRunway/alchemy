package io.rtr.alchemy.service.resources;

import com.google.inject.Inject;

import io.rtr.alchemy.dto.models.AllocationDto;
import io.rtr.alchemy.dto.requests.AllocationRequest;
import io.rtr.alchemy.mapping.Mappers;
import io.rtr.alchemy.models.Experiment;
import io.rtr.alchemy.models.Experiments;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/experiments/{experimentName}/allocations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AllocationsResource extends BaseResource {
    private final Experiments experiments;
    private final Mappers mapper;

    @Inject
    public AllocationsResource(Experiments experiments, Mappers mapper) {
        this.experiments = experiments;
        this.mapper = mapper;
    }

    @GET
    public Iterable<AllocationDto> getAllocations(
            @PathParam("experimentName") String experimentName) {
        return mapper.toDto(
                ensureExists(experiments.get(experimentName)).getAllocations(),
                AllocationDto.class);
    }

    @POST
    public void updateAllocations(
            @PathParam("experimentName") String experimentName,
            @Valid List<AllocationRequest> requests) {
        final Experiment experiment = ensureExists(experiments.get(experimentName));

        for (AllocationRequest request : requests) {
            if (request instanceof AllocationRequest.Deallocate) {
                experiment.deallocate(request.getTreatment(), request.getSize());
            } else if (request instanceof AllocationRequest.Reallocate) {
                final AllocationRequest.Reallocate reallocation =
                        (AllocationRequest.Reallocate) request;
                experiment.reallocate(
                        reallocation.getTreatment(),
                        reallocation.getTarget(),
                        reallocation.getSize());
            } else {
                experiment.allocate(request.getTreatment(), request.getSize());
            }
        }

        experiment.save();
    }

    @DELETE
    public void clearAllocations(@PathParam("experimentName") String experimentName) {
        ensureExists(experiments.get(experimentName)).deallocateAll().save();
    }
}
