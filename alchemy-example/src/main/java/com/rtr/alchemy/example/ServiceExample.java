package com.rtr.alchemy.example;

import com.rtr.alchemy.service.AlchemyService;

/**
 * This example runs an instance of the Alchemy Dropwizard with a basic configuration
 */
public class ServiceExample extends AlchemyService {
    public static void main(String[] args) throws Exception{
        AlchemyService.main(args);
    }

    @Override
    public String getName() {
        return "alchemy-example";
    }
}
