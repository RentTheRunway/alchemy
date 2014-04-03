package com.rtr.alchemy.service.health;

import com.codahale.metrics.health.HealthCheck;

public class HelloWorldCheck extends HealthCheck {
    @Override
    protected Result check() throws Exception {
        return Result.healthy("because this health check is running, we can safely assume the world still exists");
    }
}
