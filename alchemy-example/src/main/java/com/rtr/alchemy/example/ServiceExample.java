package com.rtr.alchemy.example;

import com.rtr.alchemy.service.AlchemyService;
import com.rtr.alchemy.service.config.AlchemyServiceConfigurationImpl;

/**
 * This example runs an instance of the Alchemy Dropwizard service with a basic configuration
 */
public class ServiceExample extends AlchemyService<AlchemyServiceConfigurationImpl> {
    public static void main(String[] args) throws Exception{
        new ServiceExample().run(args);
    }

    @Override
    public String getName() {
        return "alchemy-example";
    }
}
