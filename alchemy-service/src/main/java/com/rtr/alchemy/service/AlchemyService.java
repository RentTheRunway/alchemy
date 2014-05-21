package com.rtr.alchemy.service;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.rtr.alchemy.identities.Identity;
import com.rtr.alchemy.service.config.AlchemyServiceConfiguration;
import com.rtr.alchemy.service.config.IdentityMapping;
import com.rtr.alchemy.service.exceptions.RuntimeExceptionMapper;
import com.rtr.alchemy.service.guice.AlchemyModule;
import com.rtr.alchemy.service.health.ExperimentsDatabaseProviderCheck;
import com.rtr.alchemy.service.metadata.IdentitiesMetadata;
import com.rtr.alchemy.service.metadata.IdentityMetadata;
import com.rtr.alchemy.service.metrics.JmxMetricsManaged;
import com.rtr.alchemy.service.resources.ActiveTreatmentsResource;
import com.rtr.alchemy.service.resources.AllocationsResource;
import com.rtr.alchemy.service.resources.ExperimentsResource;
import com.rtr.alchemy.service.resources.MetadataResource;
import com.rtr.alchemy.service.resources.TreatmentOverridesResource;
import com.rtr.alchemy.service.resources.TreatmentsResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.Map.Entry;

/**
 * The entry point for the service
 */
public class AlchemyService extends Application<AlchemyServiceConfiguration> {

    private static final Class<?>[] RESOURCES = {
        ExperimentsResource.class,
        AllocationsResource.class,
        TreatmentOverridesResource.class,
        TreatmentsResource.class,
        ActiveTreatmentsResource.class,
        MetadataResource.class
    };

    @Override
    public void initialize(final Bootstrap<AlchemyServiceConfiguration> bootstrap) {
        bootstrap.getObjectMapper().registerModule(new GuavaModule());
    }

    @Override
    public void run(final AlchemyServiceConfiguration configuration, final Environment environment) throws Exception {
        final IdentitiesMetadata metadata = collectIdentityMetadata(configuration);
        final AlchemyModule module = new AlchemyModule(configuration, environment, metadata);
        final Injector injector = Guice.createInjector(module);

        for (final Class<?> resource : RESOURCES) {
            environment.jersey().register(injector.getInstance(resource));
        }

        environment.healthChecks().register("database", injector.getInstance(ExperimentsDatabaseProviderCheck.class));

        environment.jersey().register(new RuntimeExceptionMapper());
        environment.lifecycle().manage(new JmxMetricsManaged(environment));
        environment.lifecycle().manage(module);

        registerIdentitySubTypes(configuration, environment);
    }

    private IdentitiesMetadata collectIdentityMetadata(AlchemyServiceConfiguration configuration) {
        final IdentitiesMetadata metadata = new IdentitiesMetadata();

        for (final Entry<Class<? extends Identity>, IdentityMapping> entry : configuration.getIdentities().entrySet()) {
            final JsonTypeName typeName = entry.getValue().getDtoType().getAnnotation(JsonTypeName.class);

            Preconditions.checkNotNull(
                typeName,
                "identity DTO %s must specify @%s annotation",
                entry.getValue().getDtoType().getSimpleName(),
                JsonTypeName.class.getSimpleName()
            );

            metadata.put(
                typeName.value(),
                new IdentityMetadata(
                    typeName.value(),
                    entry.getKey(),
                    entry.getValue().getDtoType(),
                    entry.getValue().getMapperType()
                )
            );
        }

        return metadata;
    }

    private void registerIdentitySubTypes(AlchemyServiceConfiguration configuration, Environment environment) {
        for (final IdentityMapping identity : configuration.getIdentities().values()) {
            environment.getObjectMapper().registerSubtypes(identity.getDtoType());
        }
    }

    public static void main(final String[] args) throws Exception {
        new AlchemyService().run(args);
    }
}
