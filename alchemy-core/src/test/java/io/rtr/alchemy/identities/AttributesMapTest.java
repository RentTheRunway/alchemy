package io.rtr.alchemy.identities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

public class AttributesMapTest {
    private AttributesMap map;

    @Before
    public void setUp() {
        map =
                AttributesMap.newBuilder()
                        .put("true", true)
                        .put("one", 1)
                        .put("string", "string")
                        .build();
    }

    @Test
    public void testEmpty() {
        assertEquals(0, AttributesMap.empty().size());
    }

    @Test
    public void testGet() {
        assertEquals(true, map.getBoolean("true"));
        assertEquals(Long.valueOf(1), map.getNumber("one"));
        assertEquals("string", map.getString("string"));

        // wrong type
        assertNull(map.getNumber("true"));

        // does not exist
        assertNull(map.getBoolean("bad"));
    }

    @Test
    public void testGetType() {
        assertEquals(Boolean.class, map.getType("true"));
        assertEquals(Long.class, map.getType("one"));
        assertEquals(String.class, map.getType("string"));
        assertNull(map.getType("bad"));
    }

    @Test
    public void testFilter() {
        assertEquals(map.entrySet(), map.filter(map.keySet()).entrySet());
        assertTrue(map.filter(new HashSet<>()).isEmpty());
    }

    private void assertImmutable(String method, Runnable testMethod) {
        try {
            testMethod.run();
            fail(
                    String.format(
                            "method %s should not be allowed on immutable AttributesMap", method));
        } catch (UnsupportedOperationException ignored) {
        }
    }

    @Test
    public void testImmutable() {
        assertImmutable(
                "put",
                new Runnable() {
                    @Override
                    public void run() {
                        map.put("foo", "bar");
                    }
                });

        assertImmutable(
                "putAll",
                new Runnable() {
                    @Override
                    public void run() {
                        map.putAll(map);
                    }
                });

        assertImmutable(
                "remove",
                new Runnable() {
                    @Override
                    public void run() {
                        map.remove("true");
                    }
                });

        assertImmutable(
                "clear",
                new Runnable() {
                    @Override
                    public void run() {
                        map.clear();
                    }
                });
    }
}
