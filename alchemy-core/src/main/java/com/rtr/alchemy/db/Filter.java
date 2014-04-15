package com.rtr.alchemy.db;

import com.google.common.base.Objects;

/**
 * Filter criteria for a list query, allowing for pagination and filtering of items
 */
public class Filter {
    public static final Filter NONE = Filter.criteria().build();
    private final String filter;
    private final Integer offset;
    private final Integer limit;

    private Filter(String filter, Integer offset, Integer limit) {
        this.filter = filter;
        this.offset = offset;
        this.limit = limit;
    }

    public static Builder criteria() {
        return new Builder();
    }

    public static class Builder {
        private String filter;
        private Integer offset;
        private Integer limit;

        public Builder filter(String filter) {
            this.filter = filter;
            return this;
        }

        public Builder offset(Integer offset) {
            this.offset = offset;
            return this;
        }

        public Builder limit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public Filter build() {
            return new Filter(filter, offset, limit);
        }
    }

    public String getFilter() {
        return filter;
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getLimit() {
        return limit;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(filter, offset, limit);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Filter)) {
            return false;
        }

        final Filter other = (Filter) obj;

        return
            Objects.equal(filter, other.filter) &&
            Objects.equal(offset, other.offset) &&
            Objects.equal(limit, other.limit);

    }

    @Override
    public String toString() {
        return
            Objects
                .toStringHelper(this)
                .add("filter", filter)
                .add("offset", offset)
                .add("limit", limit)
                .toString();
    }
}
