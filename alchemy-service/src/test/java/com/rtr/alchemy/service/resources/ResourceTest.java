package com.rtr.alchemy.service.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.util.Types;
import com.rtr.alchemy.db.memory.MemoryStoreProvider;
import com.rtr.alchemy.dto.identities.IdentityDto;
import com.rtr.alchemy.identities.Identity;
import com.rtr.alchemy.identities.Segments;
import com.rtr.alchemy.mapping.Mapper;
import com.rtr.alchemy.mapping.Mappers;
import com.rtr.alchemy.models.Experiment;
import com.rtr.alchemy.models.Experiments;
import com.rtr.alchemy.service.mapping.CoreMappings;
import com.rtr.alchemy.service.metadata.IdentitiesMetadata;
import com.rtr.alchemy.service.metadata.IdentityMetadata;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public abstract class ResourceTest {
    private static final MemoryStoreProvider PROVIDER;
    protected static final Experiments EXPERIMENTS;
    protected static final Mappers MAPPER;
    protected static final String EXPERIMENT_1 = "pie_vs_cake";
    protected static final String EXPERIMENT_2 = "responsive_design";
    protected static final String EXPERIMENT_3 = "inactive_experiment";
    protected static final String EXPERIMENT_4 = "noallocations_experiment";
    protected static final String EXPERIMENT_BAD = "bad_experiment";
    protected static final String TREATMENT_BAD = "bad_treatment";
    protected static final String OVERRIDE_BAD = "bad_override";
    protected static final String EXP_1_TREATMENT_1 = "control";
    protected static final String EXP_1_TREATMENT_2 = "pie";
    protected static final String EXP_1_TREATMENT_3 = "cake";
    protected static final String EXP_1_OVERRIDE = "qa_pie";
    protected static final String EXP_1_OVERRIDE_USER = "qa";
    protected static final String EXP_2_TREATMENT_1 = "control";
    protected static final String EXP_2_TREATMENT_2 = "responsive";
    protected static final String EXP_2_TREATMENT_3 = "static";
    protected static final String EXP_3_TREATMENT_1 = "control";
    protected static final String EXP_4_TREATMENT_1 = "control";
    protected static final String SEGMENT_IDENTIFIED = "identified";
    protected static final String SEGMENT_DEVICE = "device";
    protected final List<ClientResponse> openResponses = Lists.newArrayList();

    @ClassRule
    public static final ResourceTestRule RESOURCES;

    static {
        PROVIDER = new MemoryStoreProvider();
        EXPERIMENTS = spy(Experiments.using(PROVIDER).build());
        MAPPER = new Mappers();
        MAPPER.register(UserDto.class, User.class, new UserMapper());
        MAPPER.register(DeviceDto.class, Device.class, new DeviceMapper());
        CoreMappings.configure(MAPPER);
        final ObjectMapper mapper = Jackson.newObjectMapper();
        mapper.registerSubtypes(UserDto.class, DeviceDto.class);
        final Environment environment = mock(Environment.class);
        doReturn(mapper).when(environment).getObjectMapper();
        final IdentitiesMetadata metadata = new IdentitiesMetadata();
        metadata.put("user", new IdentityMetadata("user", User.class, UserDto.class, UserMapper.class));
        metadata.put("device", new IdentityMetadata("device", Device.class, DeviceDto.class, DeviceMapper.class));

        RESOURCES =
            ResourceTestRule
                .builder()
                .setMapper(mapper)
                .addResource(new TreatmentsResource(EXPERIMENTS, MAPPER))
                .addResource(new AllocationsResource(EXPERIMENTS, MAPPER))
                .addResource(new TreatmentOverridesResource(EXPERIMENTS, MAPPER))
                .addResource(new ExperimentsResource(EXPERIMENTS, MAPPER))
                .addResource(new ActiveTreatmentsResource(EXPERIMENTS, MAPPER))
                .addResource(new MetadataResource(environment, metadata))
                .build();
    }

    @Before
    public void setUp() {
        PROVIDER.resetDatabase();

        EXPERIMENTS
            .create(EXPERIMENT_1)
            .setDescription("do people want pie or cake?")
            .activate()
            .setSegments(SEGMENT_IDENTIFIED)
            .addTreatment(EXP_1_TREATMENT_1)
            .addTreatment(EXP_1_TREATMENT_2)
            .addTreatment(EXP_1_TREATMENT_3)
            .allocate(EXP_1_TREATMENT_1, 50)
            .allocate(EXP_1_TREATMENT_2, 25)
            .allocate(EXP_1_TREATMENT_3, 25)
            .addOverride(EXP_1_OVERRIDE, EXP_1_TREATMENT_2, new User(EXP_1_OVERRIDE_USER))
            .save();

        EXPERIMENTS
            .create(EXPERIMENT_2)
            .activate()
            .setSegments(SEGMENT_DEVICE)
            .addTreatment(EXP_2_TREATMENT_1)
            .addTreatment(EXP_2_TREATMENT_2)
            .addTreatment(EXP_2_TREATMENT_3)
            .allocate(EXP_2_TREATMENT_1, 50)
            .allocate(EXP_2_TREATMENT_2, 25)
            .allocate(EXP_2_TREATMENT_3, 25)
            .save();

        EXPERIMENTS
            .create(EXPERIMENT_3)
            .addTreatment(EXP_3_TREATMENT_1)
            .allocate(EXP_3_TREATMENT_1, 100)
            .save();

        EXPERIMENTS
            .create(EXPERIMENT_4)
            .addTreatment(EXP_4_TREATMENT_1)
            .activate()
            .save();
    }

    @After
    public void tearDown() {
        for (final ClientResponse response : openResponses) {
            response.close();
        }
    }

    protected static <T> GenericType<Iterable<T>> iterable(Class<T> elementType) {
        return new GenericType<>(Types.newParameterizedType(List.class, elementType));
    }

    protected static <T> GenericType<Set<T>> set(Class<T> elementType) {
        return new GenericType<>(Types.newParameterizedType(Set.class, elementType));
    }

    protected static <K, V> GenericType<Map<K, V>> map(Class<K> keyType, Class<V> valueType) {
        return new GenericType<>(Types.newParameterizedType(Map.class, keyType, valueType));
    }

    private static final Pattern PATH_PARAM_PATTERN = Pattern.compile("(\\{[^}]+})");
    private static WebResource.Builder resource(String url, String ... pathParams) {
        String substUrl = url;
        Matcher matcher = PATH_PARAM_PATTERN.matcher(url);
        int index = 0;

        while (matcher.find()) {
            if (index > pathParams.length) {
                fail("missing path params");
            }

            substUrl = matcher.replaceFirst(pathParams[index++]);
            matcher = PATH_PARAM_PATTERN.matcher(substUrl);
        }

        return RESOURCES
            .client()
            .resource(substUrl)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .accept(MediaType.APPLICATION_JSON_TYPE);
    }

    protected static Experiment experiment(String name) {
        return EXPERIMENTS.get(name);
    }

    @Segments({"anonymous", "identified"})
    protected static class User extends Identity {
        private final String name;

        public User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public long computeHash(int seed) {
            return identity(seed).putString(name).hash();
        }

        @Override
        public Set<String> computeSegments() {
            return Sets.newHashSet(name == null ? "anonymous" : "identified");
        }
    }

    @JsonTypeName("user")
    protected static class UserDto extends IdentityDto {
        private final String name;

        public UserDto(@JsonProperty("name") String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private static class UserMapper implements Mapper<UserDto, User> {
        @Override
        public UserDto toDto(final User source) {
            return new UserDto(source.getName());
        }

        @Override
        public User fromDto(UserDto source) {
            return new User(source.getName());
        }
    }

    @Segments({"device"})
    protected static class Device extends Identity {
        private final String id;

        public Device(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public long computeHash(int seed) {
            return identity(seed).putString(id).hash();
        }

        @Override
        public Set<String> computeSegments() {
            return Sets.newHashSet("device");
        }
    }

    @JsonTypeName("device")
    protected static class DeviceDto extends IdentityDto {
        private final String id;

        public DeviceDto(@JsonProperty("id") String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    private static class DeviceMapper implements Mapper<DeviceDto, Device> {
        @Override
        public DeviceDto toDto(final Device source) {
            return new DeviceDto(source.getId());
        }

        @Override
        public Device fromDto(DeviceDto source) {
            return new Device(source.getId());
        }
    }

    private ResourceAssertion assertion(ClientResponse response) {
        openResponses.add(response);
        return new ResourceAssertion(response);
    }

    protected ResourceAssertion get(String url, String ... pathParams) {
        return assertion(resource(url, pathParams).get(ClientResponse.class));
    }

    protected ResourceAssertionBuilder put(String url, String ... pathParams) {
        return new ResourceAssertionBuilder(new Function<WebResource.Builder, ClientResponse>() {
            @Override
            public ClientResponse apply(WebResource.Builder input) {
                return input.put(ClientResponse.class);
            }
        }, resource(url, pathParams));
    }

    protected ResourceAssertionBuilder post(String url, String ... pathParams) {
        return new ResourceAssertionBuilder(new Function<WebResource.Builder, ClientResponse>() {
            @Override
            public ClientResponse apply(WebResource.Builder input) {
                return input.post(ClientResponse.class);
            }
        }, resource(url, pathParams));
    }

    protected ResourceAssertion delete(String url, String ... pathParams) {
        return assertion(resource(url, pathParams).delete(ClientResponse.class));
    }

    protected class ResourceAssertionBuilder {
        private final Function<WebResource.Builder, ClientResponse> action;
        private final WebResource.Builder resource;

        public ResourceAssertionBuilder(Function<WebResource.Builder, ClientResponse> action,
                                        WebResource.Builder resource) {
            this.action = action;
            this.resource = resource;
        }

        public ResourceAssertion entity(Object entity) {
            final ClientResponse response = action.apply(resource.entity(entity));
            openResponses.add(response);
            return new ResourceAssertion(response);
        }
    }

    protected static class ResourceAssertion {
        private final ClientResponse response;

        public ResourceAssertion(ClientResponse response) {
            this.response = response;
        }

        public ResourceAssertion assertStatus(Response.Status expectedStatus) {
            assertEquals("status did not match", expectedStatus.getStatusCode(), response.getStatus());
            return this;
        }

        public <T> T result(Class<T> clazz) {
            return response.getEntity(clazz);
        }

        public <T> T result(GenericType<T> type) {
            return response.getEntity(type);
        }
    }
}
