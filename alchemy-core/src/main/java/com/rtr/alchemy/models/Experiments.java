package com.rtr.alchemy.models;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.rtr.alchemy.db.BasicCacheStrategy;
import com.rtr.alchemy.db.CacheStrategy;
import com.rtr.alchemy.db.ExperimentsCache;
import com.rtr.alchemy.db.ExperimentsStoreProvider;
import com.rtr.alchemy.db.ExperimentsStore;
import com.rtr.alchemy.db.Filter;
import com.rtr.alchemy.identities.Identity;
import com.rtr.alchemy.db.CacheStrategyIterable;

import java.util.Map;

/**
 * The main class for accessing experiments
 */
public class Experiments {
    private final ExperimentsStore store;
    private final ExperimentsCache cache;
    private final CacheStrategy strategy;

    public static Builder using(ExperimentsStoreProvider provider) {
        return new Builder(provider);
    }

    private Experiments(ExperimentsStoreProvider provider, CacheStrategy strategy) {
        store = provider.getStore();
        cache = provider.getCache();
        Preconditions.checkNotNull(store, "store cannot be null");
        Preconditions.checkNotNull(cache, "cache cannot be null");
        this.strategy = strategy != null ? strategy : new BasicCacheStrategy();
    }

    public synchronized Treatment getActiveTreatment(String experimentName, Identity identity) {
        strategy.onCacheRead(experimentName, cache);

        final Experiment experiment = cache.getActiveExperiments().get(experimentName);
        if (experiment == null) {
            return null;
        }

        if (experiment.getIdentityType() != null && !experiment.getIdentityType().equals(identity.getType())) {
            return null;
        }

        final TreatmentOverride override = experiment.getOverride(identity);
        return override != null ? override.getTreatment() : experiment.getTreatment(identity);
    }

    public synchronized Iterable<Experiment> getActiveExperiments() {
        strategy.onCacheRead(cache);
        return cache.getActiveExperiments().values();
    }

    public synchronized Map<Experiment, Treatment> getActiveTreatments(Identity ... identities) {
        final Map<String, Identity> identitiesByType = Maps.newHashMap();
        for (final Identity identity : identities) {
            identitiesByType.put(identity.getType(), identity);
        }

        strategy.onCacheRead(cache);
        final Map<Experiment, Treatment> result = Maps.newHashMap();
        for (final Experiment experiment : cache.getActiveExperiments().values()) {
            if (experiment.getIdentityType() == null) {
                for (final Identity identity : identities) {
                    final TreatmentOverride override = experiment.getOverride(identity);
                    final Treatment treatment = override == null ? experiment.getTreatment(identity) : override.getTreatment();

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

                final TreatmentOverride override = experiment.getOverride(identity);
                final Treatment treatment = override == null ? experiment.getTreatment(identity) : override.getTreatment();

                if (treatment == null) {
                    continue;
                }

                result.put(experiment, treatment);
            }
        }

        return result;
    }

    public synchronized Iterable<Experiment> find(Filter filter) {
        return new CacheStrategyIterable(
            store.find(filter, new Experiment.BuilderFactory(this)),
            cache,
            strategy
        );
    }

    public synchronized Iterable<Experiment> find() {
        return find(Filter.criteria().build());
    }

    public synchronized Experiment get(String experimentName) {
        final Experiment experiment = store.load(
            experimentName,
            new Experiment.Builder(this, experimentName)
        );

        if (experiment != null) {
            strategy.onLoad(experiment, cache);
        }
        return experiment;
    }

    public synchronized void delete(String experimentName) {
        store.delete(experimentName);
        strategy.onDelete(experimentName, cache);
    }

    public synchronized void save(Experiment experiment) {
        store.save(experiment);
        strategy.onSave(experiment, cache);
    }

    public synchronized Experiment create(String name) {
        return new Experiment(this, name);
    }

    public static class Builder {
        private final ExperimentsStoreProvider provider;
        private CacheStrategy strategy;

        public Builder(ExperimentsStoreProvider provider) {
            this.provider = provider;
        }

        public Builder using(CacheStrategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public Experiments build() {
            return new Experiments(provider, strategy);
        }
    }
}
