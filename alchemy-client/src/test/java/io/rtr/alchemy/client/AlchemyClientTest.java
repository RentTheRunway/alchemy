package io.rtr.alchemy.client;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.google.common.collect.Sets;
import io.rtr.alchemy.client.dto.UserDto;
import io.rtr.alchemy.client.identities.User;
import io.rtr.alchemy.dto.identities.IdentityDto;
import io.rtr.alchemy.dto.models.AllocationDto;
import io.rtr.alchemy.dto.models.ExperimentDto;
import io.rtr.alchemy.dto.models.TreatmentDto;
import io.rtr.alchemy.dto.models.TreatmentOverrideDto;
import io.rtr.alchemy.identities.Identity;
import io.rtr.alchemy.service.AlchemyService;
import io.rtr.alchemy.service.config.AlchemyServiceConfigurationImpl;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
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
import static org.junit.Assert.assertNull;
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

    public static class WrapperService extends AlchemyService<AlchemyServiceConfigurationImpl> {
        // Needed in order for DropwizardAppRule to be able to determine configuration type
    }

    @ClassRule
    public final static DropwizardAppRule<AlchemyServiceConfigurationImpl> RULE =
        new DropwizardAppRule<>(WrapperService.class, getResourcePath("test-server.yaml"));

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
    public void testGetExperimentsFiltered() {
        client
            .createExperiment("exp")
            .setDescription("this is an experiment")
            .activate()
            .apply();

        client
            .createExperiment("another_exp")
            .setDescription("another experiment")
            .activate()
            .apply();

        final List<ExperimentDto> experiments1 = client.getExperimentsFiltered().sort("name").limit(1).apply();
        assertEquals(1, experiments1.size());
        assertEquals("another_exp", experiments1.get(0).getName());

        final List<ExperimentDto> experiments2 = client.getExperimentsFiltered().sort("name").offset(1).apply();
        assertEquals(1, experiments2.size());
        assertEquals("exp", experiments2.get(0).getName());

        final List<ExperimentDto> experiments3 = client.getExperimentsFiltered().sort("-name").offset(1).apply();
        assertEquals(1, experiments3.size());
        assertEquals("another_exp", experiments3.get(0).getName());

        final List<ExperimentDto> experiments4 = client.getExperimentsFiltered().filter("another").apply();
        assertEquals(1, experiments4.size());
        assertEquals("another_exp", experiments4.get(0).getName());
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
        } catch (final WebApplicationException e) {
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

        client.addTreatment("exp", "control");
        final TreatmentDto treatment2 = client.getTreatment("exp", "control");

        assertEquals("control", treatment2.getName());
        assertNull(treatment2.getDescription());
    }

    @Test
    public void testUpdateTreatment() {
        client
            .createExperiment("exp")
            .apply();

        client.addTreatment("exp", "control", "the control");
        client
            .updateTreatment("exp", "control")
            .setDescription("new description")
            .apply();

        final TreatmentDto treatment = client.getTreatment("exp", "control");
        assertEquals("control", treatment.getName());
        assertEquals("new description", treatment.getDescription());

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
            .addOverride("override", "control", "identified")
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
            .addOverride("override", "control", "identified")
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
            .addOverride("override", "control", "identified")
            .apply();

        client.removeOverride("exp", "override");

        assertEquals(0, client.getOverrides("exp").size());
    }

    @Test
    public void testClearOverrides() {
        client
            .createExperiment("exp")
            .addTreatment("control")
            .addOverride("override", "control", "identified")
            .apply();

        client.clearOverrides("exp");
        assertEquals(0, client.getOverrides("exp").size());
    }

    @Test
    public void testGetActiveTreatment() {
        client
            .createExperiment("exp")
            .addTreatment("control")
            .addOverride("override", "control", "identified")
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
            .addOverride("override", "control", "identified")
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

//    @Test
//    public void testGetIdentitySchema() {
//        final JsonSchema map = client.getIdentitySchema("user");
//        assertNotNull(map);
//        assertTrue(
//            map
//                .asObjectSchema()
//                .getProperties()
//                .get("name")
//                .isStringSchema()
//        );
//    }

    @Test
    public void testGetIdentityAttributes() {
        final Set<String> attributes = client.getIdentityAttributes("user");
        assertNotNull(attributes);
        assertEquals(Identity.getSupportedAttributes(User.class), attributes);
    }
}
