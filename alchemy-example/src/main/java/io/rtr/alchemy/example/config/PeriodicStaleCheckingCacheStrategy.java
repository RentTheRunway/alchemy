package io.rtr.alchemy.example.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.rtr.alchemy.caching.CacheStrategy;
import io.rtr.alchemy.service.config.CacheStrategyConfiguration;

import org.joda.time.Duration;

import javax.validation.constraints.NotNull;

public class PeriodicStaleCheckingCacheStrategy extends CacheStrategyConfiguration {
    @NotNull @JsonProperty private Duration duration;

    public Duration getDuration() {
        return duration;
    }

    @Override
    public CacheStrategy createStrategy() {
        return new io.rtr.alchemy.caching.PeriodicStaleCheckingCacheStrategy(duration);
    }
}
