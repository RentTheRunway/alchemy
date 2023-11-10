package io.rtr.alchemy.db.mongo.util;

import com.google.common.collect.AbstractIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * Ensures that if there's a problem loading an entity that we don't fail loading the rest but
 * instead just skip over it
 *
 * @param <T> The type of thing we're iterating over
 */
public class ExceptionSafeIterator<T> implements Iterator<T> {
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionSafeIterator.class);
    private final Iterator<T> abstractIterator;
    private final Iterator<T> iterator;
    private boolean nextCalled;

    private ExceptionSafeIterator(final Iterator<T> iterator) {
        this.abstractIterator =
                new AbstractIterator<T>() {
                    @Override
                    protected T computeNext() {
                        while (iterator.hasNext()) {
                            try {
                                return iterator.next();
                            } catch (final Exception e) {
                                LOG.error(
                                        "failed to retrieve next item from iterator, skipping item",
                                        e);
                            }
                        }

                        return endOfData();
                    }
                };
        this.iterator = iterator;
    }

    public static <T> ExceptionSafeIterator<T> wrap(final Iterator<T> iterator) {
        return new ExceptionSafeIterator<>(iterator);
    }

    @Override
    public boolean hasNext() {
        nextCalled = false;
        return abstractIterator.hasNext();
    }

    @Override
    public T next() {
        nextCalled = true;
        return abstractIterator.next();
    }

    // AbstractIterator<T> doesn't support remove(), because it peeks ahead and can cause ambiguity
    // as to which element
    // is being removed.  Here we make a compromise where assuming that next() has been called after
    // a hasNext(), which
    // is the most common use case, we can safely call remove()
    @Override
    public void remove() {
        if (!nextCalled) {
            // because elements are peeked in advanced, to avoid confusion as to which element is
            // being been removed
            // one must first call next() after calling hasNext() before being able to call remove()
            throw new UnsupportedOperationException(
                    "cannot remove element until next() has been called after calling hasNext()");
        }
        iterator.remove();
    }
}
