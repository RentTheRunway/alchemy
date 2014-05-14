package com.rtr.alchemy.db.mongo.models;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

/**
 * An entity for storing additional metadata
 */
@Entity(value = "Metadata", noClassnameStored = true)
public class MetadataEntity {

    @Id
    public String name;

    @Property
    public Object value;

    public static MetadataEntity of(String name, Object value) {
        return new MetadataEntity(name, value);
    }

    // Required by Morphia
    private MetadataEntity() {
    }

    private MetadataEntity(String name, Object value) {
        this.name = name;
        this.value = value;
    }
}