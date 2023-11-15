package io.rtr.alchemy.client.builder;

import com.google.common.base.Function;

import io.rtr.alchemy.dto.models.ExperimentDto;
import io.rtr.alchemy.dto.requests.GetExperimentsRequest;

import java.util.List;

public class GetExperimentsRequestBuilder {
    private final Function<GetExperimentsRequest, List<ExperimentDto>> builder;
    private String filter;
    private Integer offset;
    private Integer limit;
    private String sort;

    public GetExperimentsRequestBuilder(
            Function<GetExperimentsRequest, List<ExperimentDto>> builder) {
        this.builder = builder;
    }

    public GetExperimentsRequestBuilder filter(String filter) {
        this.filter = filter;
        return this;
    }

    public GetExperimentsRequestBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    public GetExperimentsRequestBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    public GetExperimentsRequestBuilder sort(String sort) {
        this.sort = sort;
        return this;
    }

    public List<ExperimentDto> apply() {
        return builder.apply(new GetExperimentsRequest(filter, offset, limit, sort));
    }
}
