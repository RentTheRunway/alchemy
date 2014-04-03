package com.rtr.alchemy.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.rtr.alchemy.service.config.AlchemyServiceConfiguration;
import com.rtr.alchemy.service.config.IdentityMapping;
import com.rtr.alchemy.service.guice.AlchemyModule;
import com.rtr.alchemy.service.health.HelloWorldCheck;
import com.rtr.alchemy.service.resources.DummyResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * The entry point for the service
 */
public class AlchemyService extends Application<AlchemyServiceConfiguration> {

    private static final Class<?>[] RESOURCES = {
        DummyResource.class
    };

    @Override
    public void initialize(final Bootstrap<AlchemyServiceConfiguration> bootstrap) {
    }

    @Override
    public void run(final AlchemyServiceConfiguration configuration, final Environment environment) throws Exception {
        Injector injector = Guice.createInjector(new AlchemyModule(configuration));

        for (Class<?> resource : RESOURCES) {
            environment.jersey().register(injector.getInstance(resource));
        }

        environment.healthChecks().register("HelloWorld", new HelloWorldCheck());

        registerIdentitySubTypes(configuration, environment);
    }

    private void registerIdentitySubTypes(AlchemyServiceConfiguration configuration, Environment environment) {
        for (IdentityMapping mapping : configuration.getIdentityTypes()) {
            environment.getObjectMapper().registerSubtypes(mapping.getDtoType());
        }
    }

    public static void main(final String[] args) throws Exception {
        new AlchemyService().run(args);
    }
}
