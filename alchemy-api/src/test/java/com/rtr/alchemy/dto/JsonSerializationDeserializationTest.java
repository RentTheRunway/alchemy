package com.rtr.alchemy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.rtr.alchemy.dto.identities.IdentityDto;
import com.rtr.alchemy.dto.models.AllocationDto;
import com.rtr.alchemy.dto.models.ExperimentDto;
import com.rtr.alchemy.dto.models.TreatmentDto;
import com.rtr.alchemy.dto.models.TreatmentOverrideDto;
import com.rtr.alchemy.dto.requests.AllocateRequest;
import com.rtr.alchemy.dto.requests.AllocationRequest;
import com.rtr.alchemy.dto.requests.CreateExperimentRequest;
import com.rtr.alchemy.dto.requests.TreatmentOverrideRequest;
import com.rtr.alchemy.dto.requests.UpdateExperimentRequest;
import io.dropwizard.jackson.Jackson;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JsonSerializationDeserializationTest {
    private ObjectMapper mapper;

    @Before
    public void setUp() {
        mapper = Jackson.newObjectMapper();
    }

    /**
     * This method will test whether the give json deserializes into the given object and whether the given
     * object serializes into the given json
     * @param value The object to test
     * @param resourceFile The file that contains the json to compare with
     */
    private void assertJson(Object value, String resourceFile) {
        final JsonNode jsonTree = readTreeFromResource(resourceFile);
        final JsonNode objectTree = mapper.valueToTree(value);

        assertEquals(jsonTree, objectTree);
    }

    private void assertJson(Object value) {
        final Class<?> clazz = value.getClass().isAnonymousClass() ? value.getClass().getSuperclass() : value.getClass();
        assertJson(value, clazz.getSimpleName() + ".json");
    }

    private JsonNode readTreeFromResource(String resourceFile) {
        final InputStream stream = JsonSerializationDeserializationTest.class.getClassLoader().getResourceAsStream(resourceFile);
        assertNotNull(String.format("could not load resource file %s", resourceFile), stream);

        try {
            return mapper.readTree(stream);
        } catch (final IOException e) {
            throw new AssertionError(
                String.format(
                    "could not parse json from resource file %s: %s",
                    resourceFile,
                    e.getMessage()
                )
            );
        } finally {
            try { stream.close(); } catch (final IOException ignored) { }
        }
    }

    @Test
    public void testAllocationDto() {
        assertJson(new AllocationDto(
            "control",
            20,
            10
        ));
    }

    @Test
    public void testExperimentDto() {
        assertJson(new ExperimentDto(
            "my_experiment",
            0,
            "my new experiment",
            Sets.newHashSet("identified"),
            true,
            new DateTime(0),
            new DateTime(1),
            new DateTime(2),
            new DateTime(3),
            Lists.newArrayList(
                new TreatmentDto("control", "the base case"),
                new TreatmentDto("x", "some other condition")
            ),
            Lists.newArrayList(
                new AllocationDto("control", 0, 5),
                new AllocationDto("x", 5, 10)
            ),
            Lists.newArrayList(
                new TreatmentOverrideDto(
                    "qa_override",
                    "control"
                )
            )
        ));
    }

    @Test
    public void testTreatmentDto() {
        assertJson(new TreatmentDto("control", "the base case"));
    }

    @Test
    public void testTreatmentOverrideDto() {
        assertJson(
            new TreatmentOverrideDto(
                "qa_override",
                "control"
            )
        );
    }

    @JsonTypeName("mock")
    private static class MockIdentityDto extends IdentityDto {
        private final String value;

        public MockIdentityDto(@JsonProperty("value") String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Test
    public void testIdentityDto() {
        assertJson(new MockIdentityDto("foo"));
    }

    @Test
    public void testAllocateRequest() {
        assertJson(new AllocateRequest("control", 10));
    }

    @Test
    public void testAllocationRequest() {
        assertJson(new AllocationRequest.Allocate("control", 10));
        assertJson(new AllocationRequest.Deallocate("control", 10));
        assertJson(new AllocationRequest.Reallocate("control", 10, "other"));
    }

    @Test
    public void testCreateExperimentRequest() {
        assertJson(
            new CreateExperimentRequest(
                "my_experiment",
                0,
                "my new experiment",
                Sets.newHashSet("identified"),
                true,
                Lists.newArrayList(
                    new TreatmentDto("control", "the base case"),
                    new TreatmentDto("x", "some other condition")
                ),
                Lists.newArrayList(
                        new AllocateRequest("control", 5),
                        new AllocateRequest("x", 10)
                ),
                Lists.newArrayList(
                    new TreatmentOverrideRequest("control", new MockIdentityDto("foo"), "qa_override")
                )
            )
        );
    }

    @Test
    public void testTreatmentOverrideRequest() {
        assertJson(
            new TreatmentOverrideRequest(
                "control",
                new MockIdentityDto("foo"),
                "qa_override"
            )
        );
    }

    @Test
    public void testUpdateExperimentRequest() {
        assertJson(
            new UpdateExperimentRequest(
                    Optional.<Integer>absent(),
                    Optional.of("my new experiment"),
                    Optional.of((Set<String>) Sets.newHashSet("identified")),
                    Optional.of(true),
                    Optional.<List<TreatmentDto>>of(
                        Lists.newArrayList(
                            new TreatmentDto("control", "the base case"),
                            new TreatmentDto("x", "some other condition")
                        )
                    ),
                    Optional.<List<AllocateRequest>>of(
                        Lists.newArrayList(
                            new AllocateRequest("control", 5),
                            new AllocateRequest("x", 10)
                        )
                    ),
                    Optional.<List<TreatmentOverrideRequest>>of(
                        Lists.newArrayList(
                            new TreatmentOverrideRequest("control", new MockIdentityDto("foo"), "qa_override")
                        )
                    )
            )
        );
    }

}
