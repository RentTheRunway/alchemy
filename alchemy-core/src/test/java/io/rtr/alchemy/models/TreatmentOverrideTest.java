package io.rtr.alchemy.models;

import io.rtr.alchemy.filtering.FilterExpression;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;
import javax.validation.ValidationException;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class TreatmentOverrideTest {
    @Test
    public void testEqualsHashCode() {
        EqualsVerifier
            .forClass(TreatmentOverride.class)
            .suppress(Warning.STRICT_INHERITANCE)
            .withPrefabValues(FilterExpression.class, mock(FilterExpression.class), mock(FilterExpression.class))
            .verify();
    }


    @Test
    public void testGoodName() throws ValidationException {
        final Treatment treatment =new Treatment("treatment");
        final TreatmentOverride override = new TreatmentOverride("an_ok_name", mock(FilterExpression.class),treatment);
        assertEquals(override.getName(), "an_ok_name");
    }

    @Test(expected = ValidationException.class)
    public void testBadTreatmentName() throws ValidationException {
        final Treatment treatment =new Treatment("treatment");
        final TreatmentOverride override = new TreatmentOverride("a bad name with spaces", mock(FilterExpression.class),treatment);
    }
}
