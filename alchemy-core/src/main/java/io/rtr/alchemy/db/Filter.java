package io.rtr.alchemy.db;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/** Filter criteria for a list query, allowing for pagination and filtering of items */
public class Filter {
    public static final Filter NONE = Filter.criteria().build();
    private final String filter;
    private final Integer offset;
    private final Integer limit;
    private final Ordering ordering;

    private Filter(String filter, Integer offset, Integer limit, Ordering ordering) {
        this.filter = filter;
        this.offset = offset;
        this.limit = limit;
        this.ordering = ordering;
    }

    public static Builder criteria() {
        return new Builder();
    }

    public static class Builder {
        private String filter;
        private Integer offset;
        private Integer limit;
        private Ordering ordering;

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

        public Builder ordering(Ordering ordering) {
            this.ordering = ordering;
            return this;
        }

        public Filter build() {
            return new Filter(filter, offset, limit, ordering);
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

    public Ordering getOrdering() {
        return ordering;
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

        return Objects.equal(filter, other.filter)
                && Objects.equal(offset, other.offset)
                && Objects.equal(limit, other.limit);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("filter", filter)
                .add("offset", offset)
                .add("limit", limit)
                .toString();
    }
}
