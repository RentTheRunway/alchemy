package com.rtr.alchemy.identities;

import com.google.common.collect.Sets;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Attributes(
    value = {"foo", "bar"},
    identities = { IdentityTest.Other.class }
)
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

    @Override
    public AttributesMap computeAttributes() {
        return attributes()
                .put("foo", true)
                .put("bar", false)
                .put(new Other())
                .build();
    }

    @Attributes({"baz"})
    public static class Other extends Identity {
        @Override
        public AttributesMap computeAttributes() {
            return attributes()
                    .put("baz", 1)
                    .build();
        }
    }
}
