package io.rtr.alchemy.models;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import io.rtr.alchemy.caching.BasicCacheStrategy;
import io.rtr.alchemy.caching.CacheStrategy;
import io.rtr.alchemy.caching.CachingContext;
import io.rtr.alchemy.db.ExperimentsCache;
import io.rtr.alchemy.db.ExperimentsStoreProvider;
import io.rtr.alchemy.db.ExperimentsStore;
import io.rtr.alchemy.db.Filter;
import io.rtr.alchemy.identities.AttributesMap;
import io.rtr.alchemy.identities.Identity;
import io.rtr.alchemy.caching.CacheStrategyIterable;

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

    private Treatment getTreatmentWithOverrides(Experiment experiment, Identity identity, AttributesMap attributes) {
        for (TreatmentOverride override : experiment.getOverrides()) {
            if (override.getFilter().evaluate(attributes)) {
                return override.getTreatment();
            }
        }

        return experiment.getTreatment(identity, attributes);
    }

    /**
     * Returns the current active treatment for an experiment name and identity, taking overrides into account
     */
    public Treatment getActiveTreatment(String experimentName, Identity identity) {
        strategy.onCacheRead(experimentName, context);

        final Experiment experiment = cache.getActiveExperiments().get(experimentName);
        final AttributesMap attributes = identity
                                            .computeAttributes()
                                            .filter(Identity.getSupportedAttributes(identity.getClass()));

        if (experiment == null || !experiment.getFilter().evaluate(attributes)) {
            return null;
        }

        return getTreatmentWithOverrides(experiment, identity, attributes);
    }

    /**
     * Returns all active experiments
     */
    public Iterable<Experiment> getActiveExperiments() {
        strategy.onCacheRead(context);
        return Iterables.unmodifiableIterable(cache.getActiveExperiments().values());
    }

    /**
     * Returns all active treatments for all active experiments for an identity, taking overrides into account
     */
    public Map<Experiment, Treatment> getActiveTreatments(Identity identity) {
        strategy.onCacheRead(context);
        final Map<Experiment, Treatment> result = Maps.newHashMap();
        final AttributesMap attributes = identity
                                            .computeAttributes()
                                            .filter(Identity.getSupportedAttributes(identity.getClass()));

        for (final Experiment experiment : cache.getActiveExperiments().values()) {
            if (!experiment.getFilter().evaluate(attributes)) {
                continue;
            }

            final Treatment treatment = getTreatmentWithOverrides(experiment, identity, attributes);

            if (treatment == null) {
                continue;
            }

            result.put(experiment, treatment);
        }

        return result;
    }

    /**
     * Finds an experiment given a set of criteria
     */
    public Iterable<Experiment> find(Filter filter) {
        return Iterables.unmodifiableIterable(
            new CacheStrategyIterable(
                store.find(filter, new Experiment.BuilderFactory(this)),
                context,
                strategy
            )
        );
    }

    /**
     * Finds all experiments
     */
    public Iterable<Experiment> find() {
        return Iterables.unmodifiableIterable(
            find(Filter.criteria().build())
        );
    }

    /**
     * Gets a specific experiment by name
     */
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

    /**
     * Deletes a specific experiment by name
     */
    public void delete(String experimentName) {
        store.delete(experimentName);
        strategy.onDelete(experimentName, context);
    }

    /**
     * Persists a specific experiment by name
     */
    public void save(Experiment experiment) {
        store.save(experiment);
        strategy.onSave(experiment, context);
    }

    /**
     * Creates a new experiment by name, which is not persisted until save is called
     */
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
