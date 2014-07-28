package io.rtr.alchemy.example.identities;

import io.rtr.alchemy.identities.Attributes;
import io.rtr.alchemy.identities.AttributesMap;
import io.rtr.alchemy.identities.Identity;

import java.util.Set;

/**
 * Demonstrates how one may do composite identities, which are complex identities which are made up of sub-identities
 * and have complex criteria for computing their hash
 */
@Attributes(identities = { User.class, Device.class })
public class Composite extends Identity {
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
    public long computeHash(int seed, Set<String> hashAttributes, AttributesMap attributes) {
        // we want to compute a hash based on what attribute is preferred (user vs device)
        // we'll say that 'user' supersedes 'device', such that if only 'user' is specified, we hash user,
        // if only device is specified, we hash device, if both are specified, we hash user.  If neither are
        // specified, we can return whichever is not null first user vs device

        if (hashAttributes.contains(User.ATTR_USER)) { // "user" or "both" were requested
            return user.computeHash(seed, hashAttributes, attributes);
        } else if (hashAttributes.contains(Device.ATTR_DEVICE)) { // "device" was requested
            return device.computeHash(seed, hashAttributes, attributes);
        }

        // neither was requested, default to whatever the most specifically identifying value we can return is
        if (user != null) {
            return user.computeHash(seed, hashAttributes, attributes);
        }

        if (device != null) {
            return device.computeHash(seed, hashAttributes, attributes);
        }

        return 0;
    }

    @Override
    public AttributesMap computeAttributes() {
        return attributes()
            .put(user)
            .put(device)
            .build();
    }
}
