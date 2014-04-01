package com.rtr.alchemy.models;

import org.joda.time.DateTime;

import java.util.Set;

/**
 * Represents a collection of user experiences being tested
 */
public class Experiment {
    private final String name;
    private final String description;
    private final String scope;
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
                      String scope,
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
        this.scope = scope;
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

    public String getScope() {
        return scope;
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
}
