package com.rtr.alchemy.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.rtr.alchemy.dto.jackson.AlchemyJacksonModule;
import com.rtr.alchemy.service.config.AlchemyServiceConfiguration;
import com.rtr.alchemy.service.config.IdentityMapping;
import com.rtr.alchemy.service.exceptions.RuntimeExceptionMapper;
import com.rtr.alchemy.service.guice.AlchemyModule;
import com.rtr.alchemy.service.health.ExperimentsDatabaseProviderCheck;
import com.rtr.alchemy.service.metrics.JmxMetricsManaged;
import com.rtr.alchemy.service.resources.ActiveTreatmentsResource;
import com.rtr.alchemy.service.resources.AllocationsResource;
import com.rtr.alchemy.service.resources.ExperimentsResource;
import com.rtr.alchemy.service.resources.TreatmentOverridesResource;
import com.rtr.alchemy.service.resources.TreatmentsResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * The entry point for the service
 */
public class AlchemyService extends Application<AlchemyServiceConfiguration> {

    private static final Class<?>[] RESOURCES = {
        ExperimentsResource.class,
        AllocationsResource.class,
        TreatmentOverridesResource.class,
        TreatmentsResource.class,
        ActiveTreatmentsResource.class
    };

    @Override
    public void initialize(final Bootstrap<AlchemyServiceConfiguration> bootstrap) {
        bootstrap.getObjectMapper().registerModule(new AlchemyJacksonModule());
    }

    @Override
    public void run(final AlchemyServiceConfiguration configuration, final Environment environment) throws Exception {
        final Injector injector = Guice.createInjector(new AlchemyModule(configuration));

        for (Class<?> resource : RESOURCES) {
            environment.jersey().register(injector.getInstance(resource));
        }

        environment.healthChecks().register("database", injector.getInstance(ExperimentsDatabaseProviderCheck.class));
        environment.jersey().register(new RuntimeExceptionMapper());
        environment.lifecycle().manage(new JmxMetricsManaged(environment));
        registerIdentitySubTypes(configuration, environment);
    }

    private void registerIdentitySubTypes(AlchemyServiceConfiguration configuration, Environment environment) {
        for (IdentityMapping identity : configuration.getIdentities().values()) {
            environment.getObjectMapper().registerSubtypes(identity.getDtoType());
        }
    }

    public static void main(final String[] args) throws Exception {
        new AlchemyService().run(args);
    }
}
