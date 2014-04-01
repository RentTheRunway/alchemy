package com.rtr.alchemy.db;

/**
 * Filter criteria for a list query, allowing for pagination and filtering of items
 */
public class Filter {
    private String filter;
    private Integer offset;
    private Integer limit;

    private Filter() {
    }

    public static Builder criteria() {
        return new Filter().new Builder();
    }

    public class Builder {
        public Builder filter(String filter) {
            Filter.this.filter = filter;
            return this;
        }

        public Builder offset(Integer offset) {
            Filter.this.offset = offset;
            return this;
        }

        public Builder limit(Integer limit) {
            Filter.this.limit = limit;
            return this;
        }

        public Filter build() {
            return Filter.this;
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
}
