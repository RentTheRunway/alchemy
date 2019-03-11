package io.rtr.alchemy.models;

import com.google.common.base.Objects;

import javax.validation.ValidationException;


/**
 * Represents a possible user experience in an experiment
 */



public class Treatment extends NameValidation {
    private final String name;
    private String description;

    public Treatment(String name) throws ValidationException {
        super();
        this.name = validate(name);
        this.description = null;
    }

    public Treatment(String name, String description) throws ValidationException {
        super();
        this.name = validate(name);
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Treatment)) {
            return false;
        }

        final Treatment other = (Treatment) obj;
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
                .toString();
    }
}
