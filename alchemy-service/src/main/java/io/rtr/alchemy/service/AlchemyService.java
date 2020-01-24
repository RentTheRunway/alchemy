package io.rtr.alchemy.service;

import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.rtr.alchemy.service.config.AlchemyServiceConfiguration;
import io.rtr.alchemy.service.config.IdentityMapping;
import io.rtr.alchemy.service.exceptions.RuntimeExceptionMapper;
import io.rtr.alchemy.service.filters.SparseFieldSetFilter;
import io.rtr.alchemy.service.guice.AlchemyModule;
import io.rtr.alchemy.service.health.ExperimentsDatabaseProviderCheck;
import io.rtr.alchemy.service.metrics.JmxMetricsManaged;
import io.rtr.alchemy.service.resources.ActiveTreatmentsResource;
import io.rtr.alchemy.service.resources.AllocationsResource;
import io.rtr.alchemy.service.resources.ExperimentsResource;
import io.rtr.alchemy.service.resources.MetadataResource;
import io.rtr.alchemy.service.resources.TreatmentOverridesResource;
import io.rtr.alchemy.service.resources.TreatmentsResource;
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
        Preconditions.checkState(configuration instanceof AlchemyServiceConfiguration);

        final AlchemyModule module = new AlchemyModule(configuration, environment);
        environment.lifecycle().manage(module);

        final Injector injector = Guice.createInjector(module);
        runInjected(injector, configuration, environment);
        environment.jersey().register(new SparseFieldSetFilter(environment.getObjectMapper()));
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
