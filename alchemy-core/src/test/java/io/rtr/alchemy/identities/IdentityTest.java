package io.rtr.alchemy.identities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.junit.Test;

import java.util.Set;

@Attributes(
        value = {"foo", "bar"},
        identities = {IdentityTest.Other.class})
public class IdentityTest extends Identity {
    @Test
    public void testAttributes() {
        final AttributesMap attributes = computeAttributes();

        assertEquals(Sets.newHashSet("foo", "bar", "baz"), attributes.keySet());
        assertTrue(attributes.getBoolean("foo"));
        assertFalse(attributes.getBoolean("bar"));
        assertEquals(Long.valueOf(1), attributes.getNumber("baz"));
        assertNull(attributes.getNumber("quux"));
    }

    @Test
    public void testComputeHashcodeOrderMatters() {
        final IdentityTest identity = new IdentityTest();
        final AttributesMap map =
                AttributesMap.newBuilder().put("foo", "foo").put("bar", "bar").build();
        final Set<String> fooBar = Sets.newLinkedHashSet(Lists.newArrayList("foo", "bar"));
        final Set<String> barFoo = Sets.newLinkedHashSet(Lists.newArrayList("bar", "foo"));
        final int seed = 0;

        assertEquals(
                identity.computeHash(seed, fooBar, map), identity.computeHash(seed, fooBar, map));
        assertEquals(
                identity.computeHash(seed, barFoo, map), identity.computeHash(seed, barFoo, map));

        assertNotEquals(
                identity.computeHash(seed, fooBar, map), identity.computeHash(seed, barFoo, map));
        assertNotEquals(
                identity.computeHash(seed, barFoo, map), identity.computeHash(seed, fooBar, map));
    }

    @Override
    public AttributesMap computeAttributes() {
        return attributes().put("foo", true).put("bar", false).put(new Other()).build();
    }

    @Attributes({"baz"})
    public static class Other extends Identity {
        @Override
        public AttributesMap computeAttributes() {
            return attributes().put("baz", 1).build();
        }
    }
}
