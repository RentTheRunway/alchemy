package io.rtr.alchemy.models;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import io.rtr.alchemy.filtering.FilterExpression;

/** Represents a treatment override assigned to a specific hash value */
public class TreatmentOverride implements Named {
    private final String name;
    private final FilterExpression filter;
    private final Treatment treatment;

    public TreatmentOverride(String name, FilterExpression filter, Treatment treatment) {
        this.name = name;
        this.filter = filter;
        this.treatment = treatment;
    }

    public String getName() {
        return name;
    }

    public FilterExpression getFilter() {
        return filter;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, filter, treatment);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TreatmentOverride)) {
            return false;
        }

        final TreatmentOverride other = (TreatmentOverride) obj;

        return Objects.equal(name, other.name)
                && Objects.equal(filter, other.filter)
                && Objects.equal(treatment, other.treatment);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("filter", filter)
                .add("treatment", treatment)
                .toString();
    }
}
