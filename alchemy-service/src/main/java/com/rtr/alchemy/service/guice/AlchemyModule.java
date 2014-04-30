package com.rtr.alchemy.service.guice;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.rtr.alchemy.dto.models.AllocationDto;
import com.rtr.alchemy.dto.models.ExperimentDto;
import com.rtr.alchemy.dto.models.TreatmentDto;
import com.rtr.alchemy.dto.models.TreatmentOverrideDto;
import com.rtr.alchemy.identities.Identity;
import com.rtr.alchemy.mapping.Mapper;
import com.rtr.alchemy.mapping.Mappers;
import com.rtr.alchemy.models.Allocation;
import com.rtr.alchemy.models.Experiment;
import com.rtr.alchemy.models.Experiments;
import com.rtr.alchemy.models.Treatment;
import com.rtr.alchemy.models.TreatmentOverride;
import com.rtr.alchemy.service.config.AlchemyServiceConfiguration;
import com.rtr.alchemy.service.config.IdentityMapping;
import com.rtr.alchemy.service.mapping.CoreMappings;

import java.util.Map.Entry;

/**
 * Guice module for the service
 */
public class AlchemyModule extends AbstractModule {
    private final AlchemyServiceConfiguration configuration;

    public AlchemyModule(AlchemyServiceConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
        try {
            configureImpl();
        } catch (Exception e) {
            addError("failed to configure mapper", e);
        }
    }

    private void configureImpl() throws Exception {
        final Mappers mappers = buildMappers();
        bind(Mappers.class).toInstance(mappers);

        final Experiments experiments = new Experiments(configuration.getProvider().createProvider());
        bind(Experiments.class).toInstance(experiments);
    }

    private Mappers buildMappers() throws InstantiationException, IllegalAccessException {
        final Mappers mappers = new Mappers();

        // identity types
        for (Entry<Class<? extends Identity>, IdentityMapping> mapping : configuration.getIdentities().entrySet()) {
            final Mapper mapper = mapping.getValue().getMapperType().newInstance();
            mappers.register(mapping.getValue().getDtoType(), mapping.getKey(), mapper);
        }

        CoreMappings.configure(mappers);

        return mappers;
    }
}
