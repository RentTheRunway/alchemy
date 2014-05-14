package com.rtr.alchemy.example.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rtr.alchemy.caching.CacheStrategy;
import com.rtr.alchemy.service.config.CacheStrategyConfiguration;
import io.dropwizard.util.Duration;

import javax.validation.constraints.NotNull;

public class PeriodicStaleCheckingCacheStrategy extends CacheStrategyConfiguration {
    @NotNull
    @JsonProperty
    private Duration duration;

    public Duration getDuration() {
        return duration;
    }

    @Override
    public CacheStrategy createStrategy() {
        return new com.rtr.alchemy.caching.PeriodicStaleCheckingCacheStrategy(org.joda.time.Duration.millis(duration.toMilliseconds()));
    }
}
