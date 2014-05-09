package com.rtr.alchemy.db;

import com.rtr.alchemy.models.Experiment;

import java.util.Iterator;

public class CacheStrategyIterable implements Iterable<Experiment> {
    private final Iterable<Experiment> iterable;
    private final ExperimentsCache cache;
    private final CacheStrategy strategy;

    public CacheStrategyIterable(Iterable<Experiment> iterable,
                                 ExperimentsCache cache,
                                 CacheStrategy strategy) {
        this.iterable = iterable;
        this.cache = cache;
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
            strategy.onLoad(next, cache);
            current = next;
            return next;
        }

        @Override
        public void remove() {
            iterator.remove();
            strategy.onDelete(current.getName(), cache);
        }
    }
}
