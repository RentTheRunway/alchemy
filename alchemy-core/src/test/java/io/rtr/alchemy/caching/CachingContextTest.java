package io.rtr.alchemy.caching;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import io.rtr.alchemy.db.ExperimentsCache;
import io.rtr.alchemy.models.Experiment;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class CachingContextTest {
    private CachingContext context;
    private ExperimentsCache cache;
    private ExecutorService executorService;

    @Before
    public void setUp() {
        cache = mock(ExperimentsCache.class);
        executorService = mock(ExecutorService.class);
        context = new CachingContext(cache, mock(Experiment.BuilderFactory.class), executorService);
    }

    @Test
    public void testInvalidateAll() {
        context.invalidateAll(false);
        verifyNoMoreInteractions(executorService);
        verify(cache).invalidateAll(any(Experiment.BuilderFactory.class));
    }

    @Test
    public void testInvalidateAllAsync() {
        context.invalidateAll(true);
        verify(executorService).execute(any(Runnable.class));
    }

    @Test
    public void testInvalidate() {
        context.invalidate("foo", false);
        verifyNoMoreInteractions(executorService);
        verify(cache).invalidate(eq("foo"), any());
    }

    @Test
    public void testInvalidateAsync() {
        context.invalidate("foo", true);
        verify(executorService).execute(any(Runnable.class));
    }

    @Test
    public void testClose() throws IOException {
        context.close();
        // because context doesn't own executor service, it should not be shut down
        verify(executorService, never()).shutdown();
    }
}
