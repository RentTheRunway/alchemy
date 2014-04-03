package com.rtr.alchemy.models;

import com.google.common.base.Objects;
import com.rtr.alchemy.identities.Identity;
import org.joda.time.DateTime;

import java.util.Set;

/**
 * Represents a collection of user experiences being tested
 */
public class Experiment {
    private final String name;
    private final String description;
    private final Class<? extends Identity> type;
    private final boolean active;
    private final DateTime created;
    private final DateTime modified;
    private final String owner;
    private final DateTime activated;
    private final DateTime deactivated;
    private final Set<String> groups;
    private final Set<String> allocationGroups;

    public Experiment(String name,
                      String description,
                      Class<? extends Identity> type,
                      boolean active,
                      DateTime created,
                      DateTime modified,
                      String owner,
                      DateTime activated,
                      DateTime deactivated,
                      Set<String> groups,
                      Set<String> allocationGroups) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.active = active;
        this.created = created;
        this.modified = modified;
        this.owner = owner;
        this.activated = activated;
        this.deactivated = deactivated;
        this.groups = groups;
        this.allocationGroups = allocationGroups;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Class<? extends Identity> getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public DateTime getCreated() {
        return created;
    }

    public DateTime getModified() {
        return modified;
    }

    public String getOwner() {
        return owner;
    }

    public DateTime getActivated() {
        return activated;
    }

    public DateTime getDeactivated() {
        return deactivated;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public Set<String> getAllocationGroups() {
        return allocationGroups;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Experiment)) {
            return false;
        }

        Experiment other = (Experiment) obj;
        return
            Objects.equal(name, other.name);
    }

    @Override
    public String toString() {
        return
            Objects
                .toStringHelper(this)
                .add("name", name)
                .add("description", description)
                .add("type", type)
                .add("active", active)
                .add("created", created)
                .add("modified", modified)
                .add("owner", owner)
                .add("activated", activated)
                .add("deactivated", deactivated)
                .add("groups", groups)
                .add("allocationGroups", allocationGroups)
                .toString();
    }
}
