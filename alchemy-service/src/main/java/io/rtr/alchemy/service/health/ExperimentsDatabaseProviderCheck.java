package io.rtr.alchemy.service.health;

import com.codahale.metrics.health.HealthCheck;
import com.google.inject.Inject;
import io.rtr.alchemy.db.Filter;
import io.rtr.alchemy.models.Experiments;

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
