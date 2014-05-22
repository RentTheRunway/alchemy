package com.rtr.alchemy.service;

import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.rtr.alchemy.service.config.AlchemyServiceConfiguration;
import com.rtr.alchemy.service.config.IdentityMapping;
import com.rtr.alchemy.service.exceptions.RuntimeExceptionMapper;
import com.rtr.alchemy.service.guice.AlchemyModule;
import com.rtr.alchemy.service.health.ExperimentsDatabaseProviderCheck;
import com.rtr.alchemy.service.metrics.JmxMetricsManaged;
import com.rtr.alchemy.service.resources.ActiveTreatmentsResource;
import com.rtr.alchemy.service.resources.AllocationsResource;
import com.rtr.alchemy.service.resources.ExperimentsResource;
import com.rtr.alchemy.service.resources.MetadataResource;
import com.rtr.alchemy.service.resources.TreatmentOverridesResource;
import com.rtr.alchemy.service.resources.TreatmentsResource;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * The entry point for the service
 */
public abstract class AlchemyService<T extends Configuration & AlchemyServiceConfiguration> extends Application<T> {
    private static final Class<?>[] RESOURCES = {
        ExperimentsResource.class,
        AllocationsResource.class,
        TreatmentOverridesResource.class,
        TreatmentsResource.class,
        ActiveTreatmentsResource.class,
        MetadataResource.class
    };

    @Override
    public void initialize(final Bootstrap<T> bootstrap) {
        bootstrap.getObjectMapper().registerModule(new GuavaModule());
    }

    @Override
    public void run(final T configuration, final Environment environment) throws Exception {
        assert configuration instanceof AlchemyServiceConfiguration;

        final AlchemyModule module = new AlchemyModule(configuration, environment);
        environment.lifecycle().manage(module);

        final Injector injector = Guice.createInjector(module);
        runInjected(injector, configuration, environment);

        environment.jersey().register(new RuntimeExceptionMapper());
        environment.lifecycle().manage(new JmxMetricsManaged(environment));
        registerIdentitySubTypes(configuration, environment);
    }

    protected void runInjected(final Injector injector, final T configuration, final Environment environment) throws Exception {
        for (final Class<?> resource : RESOURCES) {
            environment.jersey().register(injector.getInstance(resource));
        }

        environment.healthChecks().register("database", injector.getInstance(ExperimentsDatabaseProviderCheck.class));
    }

    private void registerIdentitySubTypes(T configuration, Environment environment) {
        for (final IdentityMapping identity : configuration.getIdentities().values()) {
            environment.getObjectMapper().registerSubtypes(identity.getDtoType());
        }
    }
}
