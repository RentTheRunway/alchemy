package com.rtr.alchemy.service.mapping;

import com.google.common.collect.Lists;
import com.rtr.alchemy.dto.models.AllocationDto;
import com.rtr.alchemy.dto.models.ExperimentDto;
import com.rtr.alchemy.dto.models.TreatmentDto;
import com.rtr.alchemy.dto.models.TreatmentOverrideDto;
import com.rtr.alchemy.mapping.Mapper;
import com.rtr.alchemy.mapping.Mappers;
import com.rtr.alchemy.models.Allocation;
import com.rtr.alchemy.models.Experiment;
import com.rtr.alchemy.models.Treatment;
import com.rtr.alchemy.models.TreatmentOverride;

public class CoreMappings {
    public static void configure(final Mappers mappers) {
        // fixed domain types
        mappers.register(
            Treatment.class,
            TreatmentDto.class,
            new Mapper<TreatmentDto, Treatment>() {
                @Override
                public TreatmentDto toDto(Treatment treatment) {
                    return new TreatmentDto(treatment.getName(), treatment.getDescription());
                }

                @Override
                public Treatment fromDto(TreatmentDto dto) {
                    throw new UnsupportedOperationException("mapping from dto not supported");
                }
            }
        );

        mappers.register(
            Allocation.class,
            AllocationDto.class,
            new Mapper<AllocationDto, Allocation>() {
                @Override
                public AllocationDto toDto(Allocation allocation) {
                    return new AllocationDto(allocation.getTreatment().getName(), allocation.getOffset(), allocation.getSize());
                }

                @Override
                public Allocation fromDto(AllocationDto source) {
                    throw new UnsupportedOperationException("mapping from dto not supported");
                }
            }
        );

        mappers.register(
            TreatmentOverride.class,
            TreatmentOverrideDto.class,
            new Mapper<TreatmentOverrideDto, TreatmentOverride>() {
                @Override
                public TreatmentOverrideDto toDto(TreatmentOverride override) {
                    return new TreatmentOverrideDto(override.getName(), override.getTreatment().getName());
                }

                @Override
                public TreatmentOverride fromDto(TreatmentOverrideDto source) {
                    throw new UnsupportedOperationException("mapping from dto not supported");
                }
            }
        );

        mappers.register(
            Experiment.class,
            ExperimentDto.class,
            new Mapper<ExperimentDto, Experiment>() {
                @Override
                public ExperimentDto toDto(Experiment experiment) {
                    return new ExperimentDto(
                        experiment.getName(),
                        experiment.getDescription(),
                        experiment.getIdentityType(),
                        experiment.isActive(),
                        experiment.getCreated(),
                        experiment.getModified(),
                        experiment.getActivated(),
                        experiment.getDeactivated(),
                        Lists.newArrayList(mappers.toDto(experiment.getTreatments(), TreatmentDto.class)),
                        Lists.newArrayList(mappers.toDto(experiment.getAllocations(), AllocationDto.class)),
                        Lists.newArrayList(mappers.toDto(experiment.getOverrides(), TreatmentOverrideDto.class))
                    );
                }

                @Override
                public Experiment fromDto(ExperimentDto source) {
                    throw new UnsupportedOperationException("mapping from dto not supported");
                }
            }
        );
    }
}
