package com.rtr.alchemy.client;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.google.common.collect.Sets;
import com.rtr.alchemy.client.dto.UserDto;
import com.rtr.alchemy.client.identities.User;
import com.rtr.alchemy.dto.identities.IdentityDto;
import com.rtr.alchemy.dto.models.AllocationDto;
import com.rtr.alchemy.dto.models.ExperimentDto;
import com.rtr.alchemy.dto.models.TreatmentDto;
import com.rtr.alchemy.dto.models.TreatmentOverrideDto;
import com.rtr.alchemy.identities.Identity;
import com.rtr.alchemy.service.AlchemyService;
import com.rtr.alchemy.service.config.AlchemyServiceConfiguration;
import com.sun.jersey.api.client.UniformInterfaceException;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AlchemyClientTest {
    private static String getResourcePath(String resourceName) {
        try {
            final URL resourceUrl = AlchemyClientTest.class.getClassLoader().getResource(resourceName);
            assertNotNull(resourceUrl);
            return (new File(resourceUrl.toURI())).getAbsolutePath();
        } catch (final Exception e) {
            throw new AssertionError(e.getMessage());
        }
    }

    @ClassRule
    public final static DropwizardAppRule<AlchemyServiceConfiguration> RULE =
        new DropwizardAppRule<>(AlchemyService.class, getResourcePath("test-server.yaml"));

    private AlchemyClient client;

    @Before
    public void setUp() throws URISyntaxException {
        final Set<Class<? extends IdentityDto>> identityTypes = Sets.newHashSet();
        identityTypes.add(UserDto.class);

        client = new AlchemyClient(new AlchemyClientConfiguration(
            new URI(String.format("http://localhost:%d", RULE.getLocalPort())),
            identityTypes
        ));
    }

    @Test
    public void testCreateExperiment() {
        client
            .createExperiment("exp")
            .setDescription("this is an experiment")
            .activate()
            .apply();

        final ExperimentDto experiment = client.getExperiment("exp");
        assertEquals("exp", experiment.getName());
        assertEquals("this is an experiment", experiment.getDescription());
        assertTrue(experiment.isActive());
    }

    @Test
    public void testGetExperiments() {
        client
            .createExperiment("exp")
            .setDescription("this is an experiment")
            .activate()
            .apply();

        final List<ExperimentDto> experiments = client.getExperiments();
        assertEquals(1, experiments.size());

        final ExperimentDto experiment = experiments.get(0);
        assertEquals("exp", experiment.getName());
        assertEquals("this is an experiment", experiment.getDescription());
        assertTrue(experiment.isActive());
    }

    @Test
    public void testUpdateExperiment() {
        client
            .createExperiment("exp")
            .activate()
            .apply();

        client
            .updateExperiment("exp")
            .setDescription("this is an experiment")
            .apply();

        final ExperimentDto experiment = client.getExperiment("exp");
        assertEquals("exp", experiment.getName());
        assertEquals("this is an experiment", experiment.getDescription());
        assertTrue(experiment.isActive());
    }

    @Test
    public void testDeleteExperiment() {
        client
            .createExperiment("exp")
            .apply();

        assertNotNull(client.getExperiment("exp"));
        client.deleteExperiment("exp");

        try {
            client.getExperiment("exp");
            fail("should have throw an exception");
        } catch (final UniformInterfaceException e) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), e.getResponse().getStatus());
        }
    }

    @Test
    public void testAddTreatment() {
        client
            .createExperiment("exp")
            .apply();

        client.addTreatment("exp", "control", "the control");

        final TreatmentDto treatment = client.getTreatment("exp", "control");
        assertEquals("control", treatment.getName());
        assertEquals("the control", treatment.getDescription());
    }

    @Test
    public void testRemoveTreatment() {
        client
            .createExperiment("exp")
            .addTreatment("control")
            .apply();

        client.removeTreatment("exp", "control");

        assertEquals(0, client.getTreatments("exp").size());
    }

    @Test
    public void testGetTreatments() {
        client
            .createExperiment("exp")
            .addTreatment("control", "the control")
            .apply();

        final List<TreatmentDto> treatments = client.getTreatments("exp");

        assertEquals(1, treatments.size());

        final TreatmentDto treatment = treatments.get(0);
        assertEquals("control", treatment.getName());
        assertEquals("the control", treatment.getDescription());
    }

    @Test
    public void testGetTreatment() {
        client
            .createExperiment("exp")
            .addTreatment("control", "the control")
            .apply();

        final TreatmentDto treatment = client.getTreatment("exp", "control");
        assertEquals("control", treatment.getName());
        assertEquals("the control", treatment.getDescription());
    }

    @Test
    public void testClearTreatments() {
        client
            .createExperiment("exp")
            .addTreatment("control")
            .apply();

        client.clearTreatments("exp");
        assertEquals(0, client.getTreatments("exp").size());
    }

    @Test
    public void testGetAllocations() {
        client
            .createExperiment("exp")
            .addTreatment("control")
            .allocate("control", 50)
            .apply();

        final List<AllocationDto> allocations = client.getAllocations("exp");

        assertEquals(1, allocations.size());

        final AllocationDto allocation = allocations.get(0);
        assertEquals("control", allocation.getTreatment());
        assertEquals(50, allocation.getSize());
    }

    @Test
    public void testUpdateAllocations() {
        client
            .createExperiment("exp")
            .addTreatment("control")
            .apply();

        client
            .updateAllocations("exp")
            .allocate("control", 100)
            .apply();

        assertEquals(1, client.getAllocations("exp").size());
    }

    @Test
    public void testClearAllocations() {
        client
            .createExperiment("exp")
            .addTreatment("control")
            .allocate("control", 10)
            .apply();

        client.clearAllocations("exp");

        assertEquals(0, client.getAllocations("exp").size());
    }

    @Test
    public void testGetOverrides() {
        client
            .createExperiment("exp")
            .addTreatment("control")
            .addOverride("override", "control", new UserDto("foo"))
            .apply();

        final List<TreatmentOverrideDto> overrides = client.getOverrides("exp");

        assertEquals(1, overrides.size());

        final TreatmentOverrideDto override = overrides.get(0);
        assertEquals("override", override.getName());
        assertEquals("control", override.getTreatment());
    }

    @Test
    public void testGetOverride() {
        client
            .createExperiment("exp")
            .addTreatment("control")
            .addOverride("override", "control", new UserDto("foo"))
            .apply();

        final TreatmentOverrideDto override = client.getOverride("exp", "override");
        assertEquals("override", override.getName());
        assertEquals("control", override.getTreatment());
    }

    @Test
    public void testRemoveOverride() {
        client
            .createExperiment("exp")
            .addTreatment("control")
            .addOverride("override", "control", new UserDto("foo"))
            .apply();

        client.removeOverride("exp", "override");

        assertEquals(0, client.getOverrides("exp").size());
    }

    @Test
    public void testClearOverrides() {
        client
            .createExperiment("exp")
            .addTreatment("control")
            .addOverride("override", "control", new UserDto("foo"))
            .apply();

        client.clearOverrides("exp");
        assertEquals(0, client.getOverrides("exp").size());
    }

    @Test
    public void testGetActiveTreatment() {
        client
            .createExperiment("exp")
            .addTreatment("control")
            .addOverride("override", "control", new UserDto("foo"))
            .activate()
            .apply();

        final TreatmentDto treatment = client.getActiveTreatment("exp", new UserDto("foo"));
        assertEquals("control", treatment.getName());
    }

    @Test
    public void testGetActiveTreatments() {
        client
            .createExperiment("exp")
            .addTreatment("control")
            .addOverride("override", "control", new UserDto("foo"))
            .activate()
            .apply();

        final Map<String, TreatmentDto> treatments = client.getActiveTreatments(new UserDto("foo"));
        assertEquals(1, treatments.size());
        assertTrue(treatments.containsKey("exp"));
        assertEquals("control", treatments.get("exp").getName());
    }

    @Test
    public void testGetIdentityTypes() {
        final Map<String, Class<? extends IdentityDto>> map = client.getIdentityTypes();
        final Class<? extends IdentityDto> userType = map.get("user");

        assertNotNull(userType);
        assertEquals(1, map.size());
        assertEquals(UserDto.class, userType);
    }

    @Test
    public void testGetIdentitySchema() {
        final JsonSchema map = client.getIdentitySchema("user");
        assertNotNull(map);
        assertTrue(
            map
                .asObjectSchema()
                .getProperties()
                .get("name")
                .isStringSchema()
        );
    }

    @Test
    public void testGetIdentitySegments() {
        final Set<String> segments = client.getIdentitySegments("user");
        assertNotNull(segments);
        assertEquals(Identity.getSupportedSegments(User.class), segments);
    }
}
