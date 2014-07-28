package io.rtr.alchemy.service.resources;

import org.junit.Test;


import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BaseResourceTest {
    @Test
    public void testCreated() {
        final Response created = BaseResource.created();
        assertEquals(Response.Status.CREATED.getStatusCode(), created.getStatus());
    }

    @Test
    public void testEnsureExists() {
        final Object value = new Object();
        assertTrue("expected same object reference value", value == BaseResource.ensureExists(value));
    }

    @Test(expected = WebApplicationException.class)
    public void testEnsureExistsThrows() {
        BaseResource.ensureExists(null);
    }
}
