package io.rtr.alchemy.dto.requests;

import java.util.ArrayList;
import java.util.Collections;

/** We need this class to deal with Jackson List serialization issues due to type-erasure */
public class AllocationRequests extends ArrayList<AllocationRequest> {
    private static final long serialVersionUID = 3619787346944661924L;

    public static AllocationRequests of(AllocationRequest... requests) {
        final AllocationRequests result = new AllocationRequests();
        Collections.addAll(result, requests);
        return result;
    }
}
