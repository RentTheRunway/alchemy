package io.rtr.alchemy.service.guice;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;

import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import io.rtr.alchemy.db.ExperimentsStoreProvider;
import io.rtr.alchemy.identities.Identity;
import io.rtr.alchemy.mapping.Mapper;
import io.rtr.alchemy.mapping.Mappers;
import io.rtr.alchemy.models.Experiments;
import io.rtr.alchemy.service.config.AlchemyServiceConfiguration;
import io.rtr.alchemy.service.config.IdentityMapping;
import io.rtr.alchemy.service.mapping.CoreMappings;
import io.rtr.alchemy.service.metadata.IdentitiesMetadata;
import io.rtr.alchemy.service.metadata.IdentityMetadata;

import java.util.Map.Entry;

/** Guice module for the service */
public class AlchemyModule extends AbstractModule implements Managed {
    private final AlchemyServiceConfiguration configuration;
    private final Environment environment;
    private final IdentitiesMetadata metadata;
    private ExperimentsStoreProvider provider;
    private Experiments experiments;

    public AlchemyModule(AlchemyServiceConfiguration configuration, Environment environment) {
        this.configuration = configuration;
        this.environment = environment;

        this.metadata = collectIdentityMetadata(configuration);
    }

    private static IdentitiesMetadata collectIdentityMetadata(
            AlchemyServiceConfiguration configuration) {
        final IdentitiesMetadata metadata = new IdentitiesMetadata();

        for (final Entry<Class<? extends Identity>, IdentityMapping> entry :
                configuration.getIdentities().entrySet()) {
            final JsonTypeName typeName =
                    entry.getValue().getDtoType().getAnnotation(JsonTypeName.class);

            Preconditions.checkNotNull(
                    typeName,
                    "identity DTO %s must specify @%s annotation",
                    entry.getValue().getDtoType().getSimpleName(),
                    JsonTypeName.class.getSimpleName());

            metadata.put(
                    typeName.value(),
                    new IdentityMetadata(
                            typeName.value(),
                            entry.getKey(),
                            entry.getValue().getDtoType(),
                            entry.getValue().getMapperType()));
        }

        return metadata;
    }

    @Override
    protected void configure() {
        try {
            configureImpl();
        } catch (final Exception e) {
            addError(String.format("failed to configure mapper: %s", e.getMessage()));
        }
    }

    private void configureImpl() throws Exception {
        bind(IdentitiesMetadata.class).toInstance(metadata);
        bind(Environment.class).toInstance(environment);
        final Mappers mappers = buildMappers();
        bind(Mappers.class).toInstance(mappers);

        provider = configuration.getProvider().createProvider();
        experiments = buildExperiments();
        bind(Experiments.class).toInstance(experiments);
    }

    private Experiments buildExperiments() {
        final Experiments.Builder builder = Experiments.using(provider);
        if (configuration.getCacheStrategy() != null) {
            builder.using(configuration.getCacheStrategy().createStrategy());
        }

        return builder.build();
    }

    private Mappers buildMappers() throws InstantiationException, IllegalAccessException {
        final Mappers mappers = new Mappers();

        // identity types
        for (final Entry<Class<? extends Identity>, IdentityMapping> mapping :
                configuration.getIdentities().entrySet()) {
            final Mapper mapper = mapping.getValue().getMapperType().newInstance();
            mappers.register(mapping.getValue().getDtoType(), mapping.getKey(), mapper);
        }

        CoreMappings.configure(mappers);

        return mappers;
    }

    @Override
    public void start() throws Exception {}

    @Override
    public void stop() throws Exception {
        provider.close();
        experiments.close();
    }
}
