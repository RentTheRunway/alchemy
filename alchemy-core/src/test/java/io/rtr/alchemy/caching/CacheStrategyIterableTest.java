package io.rtr.alchemy.caching;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.google.common.collect.Lists;

import io.rtr.alchemy.models.Experiment;

import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

public class CacheStrategyIterableTest {
    private Iterator<Experiment> iterator;
    private CachingContext context;
    private CacheStrategy strategy;
    private Experiment experiment;

    @Before
    public void setUp() {
        context = mock(CachingContext.class);
        strategy = mock(CacheStrategy.class);

        experiment = mock(Experiment.class);
        final List<Experiment> experiments = Lists.newArrayList(experiment);
        final CacheStrategyIterable iterable =
                new CacheStrategyIterable(experiments, context, strategy);
        iterator = iterable.iterator();
    }

    @Test
    public void testIterable() {
        iterator.next();
        verify(strategy).onLoad(eq(experiment), eq(context));
    }
}
