package com.rtr.alchemy.example.identities;

import com.google.common.collect.Sets;
import com.rtr.alchemy.identities.Identity;
import com.rtr.alchemy.identities.Segments;

import java.util.Set;

/**
 * Demonstrates how one may do composite identities, which are complex identities which are made up of sub-identities
 * and have complex criteria for computing their hash
 */
@Segments(
    value = { Composite.SEGMENT_USER, Composite.SEGMENT_DEVICE },
    identities = { User.class, Device.class }
)
public class Composite extends Identity {
    public static final String SEGMENT_USER = "user";
    public static final String SEGMENT_DEVICE = "device";
    private final User user;
    private final Device device;

    public Composite(User user, Device device) {
        this.user = user;
        this.device = device;
    }

    public User getUser() {
        return user;
    }

    public Device getDevice() {
        return device;
    }

    @Override
    public long computeHash(int seed, Set<String> segments) {
        // we want to compute a hash based on what segment is preferred (user vs device)
        // we'll say that 'user' supersedes 'device', such that if only 'user' is specified, we hash user,
        // if only device is specified, we hash device, if both are specified, we hash user.  If neither are
        // specified, we can return whichever is not null first user vs device

        if (segments.contains(SEGMENT_USER)) { // "user" or "both" were requested
            return user.computeHash(seed, segments);
        } else if (segments.contains(SEGMENT_DEVICE)) { // "device" was requested
            return device.computeHash(seed, segments);
        }

        // neither was requested, default to whatever the most specifically identifying value we can return is
        if (user != null) {
            return user.computeHash(seed, segments);
        }

        if (device != null) {
            return device.computeHash(seed, segments);
        }

        return 0;
    }

    @Override
    public Set<String> computeSegments() {
        return
            Sets.union(
                segments(
                    user != null ? "user" : null,
                    device != null ? "device" : null
                ),
                Sets.union(
                    segments(user),
                    segments(device)
                )
            );
    }
}
