package io.rtr.alchemy.caching;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import io.rtr.alchemy.db.ExperimentsCache;
import io.rtr.alchemy.models.Experiment;

import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Test;

public class PeriodicStaleCheckingCacheStrategyTest {
    private PeriodicStaleCheckingCacheStrategy strategy;
    private ExperimentsCache cache;
    private CachingContext context;

    @Before
    public void setUp() {
        strategy = new PeriodicStaleCheckingCacheStrategy(Duration.millis(-1));
        cache = mock(ExperimentsCache.class);
        context = spy(new CachingContext(cache, mock(Experiment.BuilderFactory.class), null));
    }

    @Test
    public void testNotStale() {
        strategy.onCacheRead(context);
        verify(context).checkIfAnyStale();
        verify(context, never()).invalidate(anyString(), anyBoolean());
        verify(context, never()).invalidateAll(anyBoolean());
        verify(cache).checkIfAnyStale();
        verify(cache, never()).invalidate(anyString(), any(Experiment.Builder.class));
        verify(cache, never()).invalidateAll(any(Experiment.BuilderFactory.class));
    }

    @Test
    public void testStale() {
        doReturn(true).when(cache).checkIfAnyStale();
        strategy.onCacheRead(context);
        verify(context).invalidateAll(eq(true));
    }

    @Test
    public void testDurationNotElapsed() {
        doReturn(true).when(cache).checkIfAnyStale();
        strategy = new PeriodicStaleCheckingCacheStrategy(Duration.standardDays(1));
        strategy.onCacheRead(context);
        verifyNoMoreInteractions(context);
        verifyNoMoreInteractions(cache);
    }
}
