package io.rtr.alchemy.caching;

import io.rtr.alchemy.models.Experiment;

import java.util.Iterator;

/**
 * Implements a wrapper around iterable of Experiment in order to trigger the cache strategy as
 * results are retrieved
 */
public class CacheStrategyIterable implements Iterable<Experiment> {
    private final Iterable<Experiment> iterable;
    private final CachingContext context;
    private final CacheStrategy strategy;

    public CacheStrategyIterable(
            Iterable<Experiment> iterable, CachingContext context, CacheStrategy strategy) {
        this.iterable = iterable;
        this.context = context;
        this.strategy = strategy;
    }

    @Override
    public Iterator<Experiment> iterator() {
        return new CacheStrategyIterator(iterable.iterator());
    }

    private class CacheStrategyIterator implements Iterator<Experiment> {
        private final Iterator<Experiment> iterator;
        private Experiment current;

        private CacheStrategyIterator(Iterator<Experiment> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Experiment next() {
            final Experiment next = iterator.next();
            strategy.onLoad(next, context);
            current = next;
            return next;
        }

        @Override
        public void remove() {
            iterator.remove();
            strategy.onDelete(current.getName(), context);
        }
    }
}
