package io.rtr.alchemy.identities;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AttributesMap implements Map<String, Object> {
    private static final AttributesMap EMPTY = new AttributesMap(Collections.emptyMap());
    private final Map<String, Object> values;

    private AttributesMap(final Map<String, Object> values) {
        this.values = Map.copyOf(values);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static AttributesMap empty() {
        return EMPTY;
    }

    @SuppressWarnings("unchecked")
    private <T> T getValue(
            final String name, final Class<?> expectedClass, final Object defaultValue) {
        final Object value = values.get(name);

        if (value == null || value.getClass() != expectedClass) {
            return (T) defaultValue;
        }

        return (T) value;
    }

    public String getString(final String name) {
        return getValue(name, String.class, null);
    }

    public String getString(final String name, final String defaultValue) {
        return getValue(name, String.class, defaultValue);
    }

    public Long getNumber(final String name) {
        return getValue(name, Long.class, null);
    }

    public Long getNumber(final String name, final long defaultValue) {
        return getValue(name, Long.class, defaultValue);
    }

    public Boolean getBoolean(final String name) {
        return getValue(name, Boolean.class, null);
    }

    public Boolean getBoolean(final String name, final boolean defaultValue) {
        return getValue(name, Boolean.class, defaultValue);
    }

    public Class<?> getType(final String name) {
        final Object value = values.get(name);
        return value != null ? value.getClass() : null;
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean containsKey(final Object o) {
        return values.containsKey(o);
    }

    @Override
    public boolean containsValue(final Object o) {
        return values.containsValue(o);
    }

    @Override
    public Object get(final Object o) {
        return values.get(o);
    }

    @Override
    public Object put(final String s, final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(final Map<? extends String, ?> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> keySet() {
        return values.keySet();
    }

    @Override
    public Collection<Object> values() {
        return values.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return values.entrySet();
    }

    public AttributesMap filter(final Set<String> keys) {
        final Map<String, Object> builder = new HashMap<>();
        for (final Entry<String, Object> entry : entrySet()) {
            if (keys.contains(entry.getKey())) {
                builder.put(entry.getKey(), entry.getValue());
            }
        }

        return new AttributesMap(Map.copyOf(builder));
    }

    public static class Builder {
        private final Map<String, Object> builder = new HashMap<>();

        public Builder put(final String name, final String value) {
            if (value != null) {
                builder.put(name, value);
            }
            return this;
        }

        public Builder put(final String name, final byte value) {
            builder.put(name, (long) value);
            return this;
        }

        public Builder put(final String name, final short value) {
            builder.put(name, (long) value);
            return this;
        }

        public Builder put(final String name, final int value) {
            builder.put(name, (long) value);
            return this;
        }

        public Builder put(final String name, final long value) {
            builder.put(name, value);
            return this;
        }

        public Builder put(final String name, final boolean value) {
            builder.put(name, value);
            return this;
        }

        public Builder put(final Identity identity) {
            if (identity != null) {
                builder.putAll(identity.computeAttributes().values);
            }
            return this;
        }

        public AttributesMap build() {
            return new AttributesMap(Map.copyOf(builder));
        }
    }
}
