package io.rtr.alchemy.db.mongo.models;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;

/** An entity for storing additional metadata */
@Entity(value = "Metadata", useDiscriminator = false)
public class MetadataEntity {

    @Id public String name;

    @Property public Object value;

    // Required by Morphia
    @SuppressWarnings("unused")
    private MetadataEntity() {}

    private MetadataEntity(final String name, final Object value) {
        this.name = name;
        this.value = value;
    }

    public static MetadataEntity of(final String name, final Object value) {
        return new MetadataEntity(name, value);
    }
}
