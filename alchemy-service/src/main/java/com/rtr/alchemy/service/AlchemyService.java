package com.rtr.alchemy.service;

import com.rtr.alchemy.identities.Identity;
import com.rtr.alchemy.service.config.AlchemyServiceConfiguration;
import com.rtr.alchemy.service.identities.IdentityMixIn;
import com.rtr.alchemy.service.resources.DummyResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * The entry point for the service
 */
public class AlchemyService extends Application<AlchemyServiceConfiguration> {
    @Override
    public void initialize(final Bootstrap<AlchemyServiceConfiguration> bootstrap) {
        bootstrap.getObjectMapper().addMixInAnnotations(Identity.class, IdentityMixIn.class);
    }

    @Override
    public void run(final AlchemyServiceConfiguration configuration, final Environment environment) throws Exception {
        environment.jersey().register(new DummyResource());
    }

    public static void main(final String[] args) throws Exception {
        new AlchemyService().run(args);
    }
}
