package io.rtr.alchemy.identities;

import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class AttributesMap implements Map<String, Object> {
    private static final AttributesMap EMPTY = new AttributesMap(ImmutableMap.<String, Object>of());
    private final ImmutableMap<String, Object> values;

    private AttributesMap(ImmutableMap<String, Object> values) {
        this.values = values;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static AttributesMap empty() {
        return EMPTY;
    }

    @SuppressWarnings("unchecked")
    private <T> T getValue(String name, Class<?> expectedClass, Object defaultValue) {
        final Object value = values.get(name);

        if (value == null || value.getClass() != expectedClass) {
            return (T) defaultValue;
        }

        return (T) value;
    }

    public String getString(String name) {
        return getValue(name, String.class, null);
    }

    public String getString(String name, String defaultValue) {
        return getValue(name, String.class, defaultValue);
    }

    public Long getNumber(String name) {
        return getValue(name, Long.class, null);
    }

    public Long getNumber(String name, long defaultValue) {
        return getValue(name, Long.class, defaultValue);
    }

    public Boolean getBoolean(String name) {
        return getValue(name, Boolean.class, null);
    }

    public Boolean getBoolean(String name, boolean defaultValue) {
        return getValue(name, Boolean.class, defaultValue);
    }

    public Class<?> getType(String name) {
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
    public boolean containsKey(Object o) {
        return values.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return values.containsValue(o);
    }

    @Override
    public Object get(Object o) {
        return values.get(o);
    }

    @Override
    public Object put(String s, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ?> map) {
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

    public AttributesMap filter(Set<String> keys) {
        final ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        for (Entry<String, Object> entry : entrySet()) {
            if (keys.contains(entry.getKey())) {
                builder.put(entry);
            }
        }

        return new AttributesMap(builder.build());
    }

    public static class Builder {
        private final ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();

        public Builder put(String name, String value) {
            if (value != null) {
                builder.put(name, value);
            }
            return this;
        }

        public Builder put(String name, byte value) {
            builder.put(name, (long) value);
            return this;
        }

        public Builder put(String name, short value) {
            builder.put(name, (long) value);
            return this;
        }

        public Builder put(String name, int value) {
            builder.put(name, (long) value);
            return this;
        }

        public Builder put(String name, long value) {
            builder.put(name, value);
            return this;
        }

        public Builder put(String name, boolean value) {
            builder.put(name, value);
            return this;
        }

        public Builder put(Identity identity) {
            if (identity != null) {
                builder.putAll(identity.computeAttributes().values);
            }
            return this;
        }

        public AttributesMap build() {
            return new AttributesMap(builder.build());
        }
    }
}
