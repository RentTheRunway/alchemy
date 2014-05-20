package com.rtr.alchemy.service.health;

import com.codahale.metrics.health.HealthCheck;
import com.google.inject.Inject;
import com.rtr.alchemy.db.Filter;
import com.rtr.alchemy.models.Experiments;

/**
 * A health check that tests whether experiments can be read from the store
 */
public class ExperimentsDatabaseProviderCheck extends HealthCheck {
    private final Experiments experiments;

    @Inject
    public ExperimentsDatabaseProviderCheck(Experiments experiments) {
        this.experiments = experiments;
    }

    @Override
    protected Result check() throws Exception {
        experiments.find(Filter.criteria().limit(1).build());
        experiments.getActiveExperiments();
        return Result.healthy();
    }
}
