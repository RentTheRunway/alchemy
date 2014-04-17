package com.rtr.alchemy.models;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.rtr.alchemy.db.ExperimentsCache;
import com.rtr.alchemy.db.ExperimentsDatabaseProvider;
import com.rtr.alchemy.db.ExperimentsStore;
import com.rtr.alchemy.db.Filter;
import com.rtr.alchemy.identities.Identity;

import java.util.Map;

/**
 * The main class for accessing experiments
 */
public class Experiments {
    private final ExperimentsStore store;
    private final ExperimentsCache cache;

    public Experiments(ExperimentsDatabaseProvider provider) {
        store = provider.createStore();
        cache = provider.createCache();
        Preconditions.checkNotNull(store, "store cannot be null");
        Preconditions.checkNotNull(cache, "cache cannot be null");
    }

    public Treatment getActiveTreatment(String experimentName, Identity identity) {
        final Experiment experiment = cache.getActiveExperiments().get(experimentName);
        if (experiment == null) {
            return null;
        }

        if (experiment.getIdentityType() != null && !experiment.getIdentityType().equals(identity.getType())) {
            return null;
        }

        final TreatmentOverride override = experiment.getOverride(identity);
        if (override != null) {
            return override.getTreatment();
        }
        return experiment.getTreatment(identity);
    }

    public Iterable<Experiment> getActiveExperiments() {
        return cache.getActiveExperiments().values();
    }

    public Map<Experiment, Treatment> getActiveTreatments(Identity ... identities) {
        final Map<String, Identity> identitiesByType = Maps.newHashMap();
        for (Identity identity : identities) {
            identitiesByType.put(identity.getType(), identity);
        }

        final Map<Experiment, Treatment> result = Maps.newHashMap();
        for (Experiment experiment : getActiveExperiments()) {
            if (experiment.getIdentityType() == null) {
                for (Identity identity : identities) {
                    final Treatment treatment = experiment.getTreatment(identity);

                    if (treatment != null) {
                        result.put(experiment, treatment);
                        break;
                    }
                }
            } else {
                final Identity identity = identitiesByType.get(experiment.getIdentityType());
                if (identity == null) {
                    continue;
                }

                final Treatment treatment = experiment.getTreatment(identity);

                if (treatment == null) {
                    continue;
                }

                result.put(experiment, treatment);
            }
        }

        return result;
    }

    public Iterable<Experiment> find(Filter filter) {
        return store.find(filter);
    }

    public Iterable<Experiment> find() {
        return store.find(Filter.criteria().build());
    }

    public Experiment get(String experimentName) {
        return store.load(
            experimentName,
            new Experiment.Builder(store, experimentName)
        );
    }

    public void delete(String experimentName) {
        store.delete(experimentName);
    }

    public Experiment create(String name) {
        return new Experiment(store, name);
    }
}
