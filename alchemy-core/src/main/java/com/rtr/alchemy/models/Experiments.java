package com.rtr.alchemy.models;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.rtr.alchemy.caching.BasicCacheStrategy;
import com.rtr.alchemy.caching.CacheStrategy;
import com.rtr.alchemy.caching.CachingContext;
import com.rtr.alchemy.db.ExperimentsCache;
import com.rtr.alchemy.db.ExperimentsStoreProvider;
import com.rtr.alchemy.db.ExperimentsStore;
import com.rtr.alchemy.db.Filter;
import com.rtr.alchemy.identities.Identity;
import com.rtr.alchemy.caching.CacheStrategyIterable;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * The main class for accessing experiments
 */
public class Experiments implements Closeable {
    private final ExperimentsStore store;
    private final ExperimentsCache cache;
    private final CacheStrategy strategy;
    private final CachingContext context;

    public static Builder using(ExperimentsStoreProvider provider) {
        return new Builder(provider);
    }

    private Experiments(ExperimentsStoreProvider provider,
                        CacheStrategy strategy,
                        ExecutorService executorService) {
        store = provider.getStore();
        cache = provider.getCache();
        Preconditions.checkNotNull(store, "store cannot be null");
        Preconditions.checkNotNull(cache, "cache cannot be null");
        this.strategy = strategy != null ? strategy : new BasicCacheStrategy();
        this.context = new CachingContext(cache, new Experiment.BuilderFactory(this), executorService);
        cache.invalidateAll(new Experiment.BuilderFactory(this));
    }

    public Treatment getActiveTreatment(String experimentName, Identity identity) {
        strategy.onCacheRead(experimentName, context);

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

    public Iterable<Experiment> getActiveExperiments() {
        strategy.onCacheRead(context);
        return Iterables.unmodifiableIterable(cache.getActiveExperiments().values());
    }

    public Map<Experiment, Treatment> getActiveTreatments(Identity ... identities) {
        final Map<String, Identity> identitiesByType = Maps.newHashMap();
        for (final Identity identity : identities) {
            identitiesByType.put(identity.getType(), identity);
        }

        strategy.onCacheRead(context);
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

    public Iterable<Experiment> find(Filter filter) {
        return Iterables.unmodifiableIterable(
            new CacheStrategyIterable(
                store.find(filter, new Experiment.BuilderFactory(this)),
                context,
                strategy
            )
        );
    }

    public Iterable<Experiment> find() {
        return Iterables.unmodifiableIterable(
            find(Filter.criteria().build())
        );
    }

    public Experiment get(String experimentName) {
        final Experiment experiment = store.load(
            experimentName,
            new Experiment.Builder(this, experimentName)
        );

        if (experiment != null) {
            strategy.onLoad(experiment, context);
        }
        return experiment;
    }

    public void delete(String experimentName) {
        store.delete(experimentName);
        strategy.onDelete(experimentName, context);
    }

    public void save(Experiment experiment) {
        store.save(experiment);
        strategy.onSave(experiment, context);
    }

    public Experiment create(String name) {
        return new Experiment(this, name);
    }

    @Override
    public void close() throws IOException {
        context.close();
    }

    public static class Builder {
        private final ExperimentsStoreProvider provider;
        private CacheStrategy strategy;
        private ExecutorService executorService;

        public Builder(ExperimentsStoreProvider provider) {
            this.provider = provider;
        }

        public Builder using(CacheStrategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public Builder using(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public Experiments build() {
            return new Experiments(provider, strategy, executorService);
        }
    }
}
