package io.rtr.alchemy.db;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrderingTest {
    @Test
    public void testParse() {
        // null expression
        assertEquals(Ordering.empty().getFields(), Ordering.parse(null).getFields());

        // empty expression
        assertEquals(Ordering.empty().getFields(), Ordering.parse("").getFields());

        // single field expression
        assertEquals(
                ImmutableMap.of(Ordering.Field.NAME, Ordering.Direction.ASCENDING),
                Ordering.parse("name").getFields());

        // single descending field expression
        assertEquals(
                ImmutableMap.of(Ordering.Field.NAME, Ordering.Direction.DESCENDING),
                Ordering.parse("-name").getFields());

        // multiple fields
        assertEquals(
                ImmutableMap.of(
                        Ordering.Field.ACTIVE, Ordering.Direction.ASCENDING,
                        Ordering.Field.NAME, Ordering.Direction.DESCENDING,
                        Ordering.Field.CREATED, Ordering.Direction.ASCENDING),
                Ordering.parse("active,-name,created").getFields());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseUnknown() {
        Ordering.parse("unknown");
    }

    @Test
    public void testBuilder() {
        // empty expression
        assertEquals(Ordering.empty().getFields(), Ordering.newBuilder().build().getFields());

        // single field expression
        assertEquals(
                ImmutableMap.of(Ordering.Field.NAME, Ordering.Direction.ASCENDING),
                Ordering.newBuilder().orderBy(Ordering.Field.NAME).build().getFields());

        // single descending field expression
        assertEquals(
                ImmutableMap.of(Ordering.Field.NAME, Ordering.Direction.DESCENDING),
                Ordering.newBuilder()
                        .orderBy(Ordering.Field.NAME, Ordering.Direction.DESCENDING)
                        .build()
                        .getFields());

        // multiple fields
        assertEquals(
                ImmutableMap.of(
                        Ordering.Field.ACTIVE, Ordering.Direction.ASCENDING,
                        Ordering.Field.NAME, Ordering.Direction.DESCENDING,
                        Ordering.Field.CREATED, Ordering.Direction.ASCENDING),
                Ordering.newBuilder()
                        .orderBy(Ordering.Field.ACTIVE, Ordering.Direction.ASCENDING)
                        .orderBy(Ordering.Field.NAME, Ordering.Direction.DESCENDING)
                        .orderBy(Ordering.Field.CREATED)
                        .build()
                        .getFields());
    }
}
