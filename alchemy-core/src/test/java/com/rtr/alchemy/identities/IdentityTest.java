package com.rtr.alchemy.identities;

import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@Segments(
    value = {"foo", "bar"},
    identities = { IdentityTest.Other.class }
)
public class IdentityTest extends Identity {
    @Override
    public long computeHash(int seed, Set<String> segments) {
        return 0;
    }

    @Test
    public void testSegments() {
        assertEquals(EMPTY, this.segments((String) null));
        assertEquals(EMPTY, this.segments((Identity) null));
        assertEquals(Sets.newHashSet("foo", "bar"), this.segments("foo", null, "bar"));

        final Identity identity = mock(Identity.class);
        doReturn(Sets.newHashSet("foo")).when(identity).computeSegments();
        assertEquals(Sets.newHashSet("foo"), this.segments(null, identity, null));

        assertEquals(Sets.newHashSet("baz"), Identity.getSupportedSegments(Other.class));
        assertEquals(Sets.newHashSet("foo", "bar", "baz"), Identity.getSupportedSegments(IdentityTest.class));
    }

    @Segments({"baz"})
    public static class Other extends Identity {
        @Override
        public long computeHash(int seed, Set<String> segments) {
            return 0;
        }
    }
}
