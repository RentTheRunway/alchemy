package com.rtr.alchemy.dto.identities;

import java.util.ArrayList;
import java.util.Collections;

/**
 * We need this class to deal with Jackson List serialization issues due to type-erasure
 */
public class Identities  extends ArrayList<IdentityDto> {
    private static final long serialVersionUID = 8406201398658647047L;

    public static Identities of(IdentityDto ... requests) {
        final Identities result = new Identities();
        Collections.addAll(result, requests);
        return result;
    }

    public static Identities empty() {
        return new Identities();
    }
}

