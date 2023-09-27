package io.rtr.alchemy.dto.requests;

/** Represents a request for getting filtered experiment */
public class GetExperimentsRequest {
    private final String filter;
    private final Integer offset;
    private final Integer limit;
    private final String sort;

    public GetExperimentsRequest(String filter, Integer offset, Integer limit, String sort) {
        this.filter = filter;
        this.offset = offset;
        this.limit = limit;
        this.sort = sort;
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

    public String getSort() {
        return sort;
    }
}
