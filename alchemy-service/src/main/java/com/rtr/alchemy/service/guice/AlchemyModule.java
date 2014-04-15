package com.rtr.alchemy.service.guice;

import com.google.inject.AbstractModule;
import com.rtr.alchemy.dto.models.ExperimentDto;
import com.rtr.alchemy.mapping.Mapper;
import com.rtr.alchemy.mapping.MapperBuilder;
import com.rtr.alchemy.models.Experiment;
import com.rtr.alchemy.service.config.AlchemyServiceConfiguration;
import com.rtr.alchemy.service.config.IdentityMapping;

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
            final Mapper mapper = buildMapper();
            bind(Mapper.class).toInstance(mapper);
        } catch (InstantiationException | IllegalAccessException e) {
            addError("failed to configure mapper", e);
        }
    }

    private Mapper buildMapper() throws InstantiationException, IllegalAccessException {
        MapperBuilder mapperBuilder = configuration.getMapper().newInstance();

        // custom identity types
        for (IdentityMapping mapping : configuration.getIdentityTypes()) {
            mapperBuilder = mapperBuilder.register(
                mapping.getDtoType(),
                mapping.getIdentityType()
            );
        }

        // fixed domain types
        mapperBuilder = mapperBuilder.register(Experiment.class, ExperimentDto.class);

        return mapperBuilder.build();
    }
}
