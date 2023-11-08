package io.rtr.alchemy.db.mongo.models;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;

/** An entity for storing additional metadata */
@Entity(value = "Metadata", noClassnameStored = true)
public class MetadataEntity {

    @Id public String name;

    @Property public Object value;

    public static MetadataEntity of(String name, Object value) {
        return new MetadataEntity(name, value);
    }

    // Required by Morphia
    private MetadataEntity() {}

    private MetadataEntity(String name, Object value) {
        this.name = name;
        this.value = value;
    }
}
