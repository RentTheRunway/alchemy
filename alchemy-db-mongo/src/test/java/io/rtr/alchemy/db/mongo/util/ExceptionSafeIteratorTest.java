package io.rtr.alchemy.db.mongo.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExceptionSafeIteratorTest {
    @Test
    public void testIteratorWithExceptions() {
        final Iterator<Integer> throwIterator = new Iterator<Integer>() {
            private final Integer[] numbers = { 0, 1, null, 2, null, null, 3, null, null };
            private int n = 0;

            @Override
            public boolean hasNext() {
                return n < numbers.length;
            }

            @Override
            public Integer next() {
                if (numbers[n] == null) {
                    n++;
                    throw new UnsupportedOperationException();
                }

                return numbers[n++];
            }

            @Override
            public void remove() {
            }
        };

        final Iterator<Integer> evens = ExceptionSafeIterator.wrap(throwIterator);

        assertArrayEquals(new Integer[]{0, 1, 2, 3}, Iterators.toArray(evens, Integer.class));
    }

    @Test(expected = NoSuchElementException.class)
    public void testIteratorExhausted() {
        final Iterator<Integer> emptyIterator = ExceptionSafeIterator.wrap(Collections.<Integer>emptyIterator());
        assertFalse(emptyIterator.hasNext());
        emptyIterator.next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIteratorRemoveWithoutNext() {
        final Iterator<Integer> iterator = ExceptionSafeIterator.wrap(Lists.newArrayList(1, 2, 3).iterator());
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.remove();
    }

    @Test
    public void testIteratorRemove() {
        final List<Integer> numbers = Lists.newArrayList(1, 2, 3);
        final Iterator<Integer> iterator = ExceptionSafeIterator.wrap(numbers.iterator());
        assertTrue(iterator.hasNext());
        iterator.next();
        iterator.remove();

        assertEquals(Lists.newArrayList(2, 3), numbers);
    }
}
