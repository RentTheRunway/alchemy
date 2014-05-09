package com.rtr.alchemy.db;

import com.google.common.collect.Lists;
import com.rtr.alchemy.models.Experiment;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CacheStrategyIterableTest {
    private Iterator<Experiment> iterator;
    private ExperimentsCache cache;
    private CacheStrategy strategy;
    private Experiment experiment;

    @Before
    public void setUp() {
        cache = mock(ExperimentsCache.class);
        strategy = mock(CacheStrategy.class);

        experiment = mock(Experiment.class);
        final List<Experiment> experiments = Lists.newArrayList(experiment);
        final CacheStrategyIterable iterable = new CacheStrategyIterable(experiments, cache, strategy);
        iterator = iterable.iterator();
    }

    @Test
    public void testIterable() {
        iterator.next();
        verify(strategy).onLoad(eq(experiment), eq(cache));
    }
}
