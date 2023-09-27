package io.rtr.alchemy.service.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import io.rtr.alchemy.dto.identities.IdentityDto;
import io.rtr.alchemy.dto.models.TreatmentDto;
import io.rtr.alchemy.identities.Identity;
import io.rtr.alchemy.mapping.Mappers;
import io.rtr.alchemy.models.Experiment;
import io.rtr.alchemy.models.Experiments;
import io.rtr.alchemy.models.Treatment;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

/** Resource for querying active experiments */
@Path("/active")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ActiveTreatmentsResource extends BaseResource {
    private final Experiments experiments;
    private final Mappers mapper;

    @Inject
    public ActiveTreatmentsResource(Experiments experiments, Mappers mapper) {
        this.experiments = experiments;
        this.mapper = mapper;
    }

    @POST
    @Timed
    @Path("/experiments/{experimentName}/treatment")
    public TreatmentDto getActiveTreatment(
            @PathParam("experimentName") String experimentName, @Valid IdentityDto identity) {
        return mapper.toDto(
                experiments.getActiveTreatment(
                        experimentName, mapper.fromDto(identity, Identity.class)),
                TreatmentDto.class);
    }

    @POST
    @Timed
    @Path("/treatments")
    public Map<String, TreatmentDto> getActiveTreatments(@Valid IdentityDto identityDto) {
        final Map<String, TreatmentDto> treatments = Maps.newHashMap();
        final Map<Experiment, Treatment> activeTreatments =
                experiments.getActiveTreatments(mapper.fromDto(identityDto, Identity.class));

        for (final Map.Entry<Experiment, Treatment> entry : activeTreatments.entrySet()) {
            treatments.put(
                    entry.getKey().getName(), mapper.toDto(entry.getValue(), TreatmentDto.class));
        }

        return treatments;
    }
}
