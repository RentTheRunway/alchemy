package io.rtr.alchemy.db;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Specifies how multiple fields are ordered
 */
public class Ordering {
    private final Map<Field, Direction> fields;

    private Ordering(Map<Field, Direction> fields) {
        this.fields = Collections.unmodifiableMap(fields);
    }

    public Map<Field, Direction> getFields() {
        return fields;
    }

    public boolean isEmpty() {
        return fields.isEmpty();
    }

    public static Ordering parse(String ordering) {
        if (ordering == null) {
            return Ordering.empty();
        }

        final StringTokenizer tokenizer = new StringTokenizer(ordering, ",");
        final Map<Field, Direction> fields = Maps.newLinkedHashMap();

        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken();
            final int index = token.indexOf('-');
            final Direction direction = index > -1 ? Direction.DESCENDING : Direction.ASCENDING;
            final Field field = Field.fromName(token.substring(index + 1));

            Preconditions.checkArgument(
                field != null,
                "Unsupported ordering field: %s",
                token
            );

            fields.put(field, direction);
        }

        return new Ordering(fields);
    }

    public static Ordering empty() {
        return new Ordering(Maps.<Field, Direction>newHashMap());
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static enum Direction {
        ASCENDING,
        DESCENDING
    }

    public static enum Field {
        NAME("name"),
        DESCRIPTION("description"),
        CREATED("created"),
        MODIFIED("modified"),
        ACTIVATED("activated"),
        DEACTIVATED("deactivated"),
        ACTIVE("active");

        private final String name;
        private static final Map<String, Field> FIELDS_BY_NAME;

        static {
            FIELDS_BY_NAME = Maps.newHashMap();
            for (Field field : Field.values()) {
                FIELDS_BY_NAME.put(field.name, field);
            }
        }

        Field(String name) {
            this.name = name;
        }

        public static Field fromName(String name) {
            return FIELDS_BY_NAME.get(name);
        }

        public String getName() {
            return name;
        }
    }

    public static class Builder {
        private final Map<Field, Direction> ordering = Maps.newLinkedHashMap();

        public Builder orderBy(Field field) {
            ordering.put(field, Direction.ASCENDING);
            return this;
        }

        public Builder orderBy(Field field, Direction direction) {
            ordering.put(field, direction);
            return this;
        }

        public Ordering build() {
            return new Ordering(ordering);
        }
    }
}
