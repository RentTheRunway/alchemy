package com.rtr.alchemy.client;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.rtr.alchemy.client.builder.CreateExperimentRequestBuilder;
import com.rtr.alchemy.client.builder.UpdateAllocationsRequestBuilder;
import com.rtr.alchemy.client.builder.UpdateExperimentRequestBuilder;
import com.rtr.alchemy.dto.identities.IdentityDto;
import com.rtr.alchemy.dto.models.AllocationDto;
import com.rtr.alchemy.dto.models.ExperimentDto;
import com.rtr.alchemy.dto.models.TreatmentDto;
import com.rtr.alchemy.dto.models.TreatmentOverrideDto;
import com.rtr.alchemy.dto.requests.TreatmentOverrideRequest;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.setup.Environment;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.annotation.Nullable;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A Dropwizard client for talking to an instance Alchemy service
 */
public class AlchemyClient {
    private static final String CLIENT_NAME = "alchemy-client";
    private static final Map<String, ?> EMPTY_PARAMS = Maps.newHashMap();
    private static final ClassTypeMappper CLASS_TYPE_MAPPPER = new ClassTypeMappper();
    private final MetricRegistry metricRegistry = new MetricRegistry();
    private final JerseyClientBuilder clientBuilder = new JerseyClientBuilder(metricRegistry);
    private final Client client;
    private final AlchemyClientConfiguration configuration;

    private static final String PARAM_EXPERIMENT_NAME = "experimentName";
    private static final String PARAM_TREATMENT_NAME = "treatmentName";
    private static final String PARAM_OVERRIDE_NAME = "overrideName";
    private static final String PARAM_IDENTITY_TYPE_NAME = "identityType";

    private static final String ENDPOINT_EXPERIMENTS = "/experiments";
    private static final String ENDPOINT_EXPERIMENT = "/experiments/{experimentName}";
    private static final String ENDPOINT_ALLOCATIONS = "/experiments/{experimentName}/allocations";
    private static final String ENDPOINT_TREATMENTS = "/experiments/{experimentName}/treatments";
    private static final String ENDPOINT_TREATMENT = "/experiments/{experimentName}/treatments/{treatmentName}";
    private static final String ENDPOINT_OVERRIDES = "/experiments/{experimentName}/overrides";
    private static final String ENDPOINT_OVERRIDE = "/experiments/{experimentName}/overrides/{overrideName}";
    private static final String ENDPOINT_ACTIVE_TREATMENT = "/active/experiments/{experimentName}/treatment";
    private static final String ENDPOINT_ACTIVE_TREATMENTS = "/active/treatments";
    private static final String ENDPOINT_METADATA_IDENTITY_TYPES = "/metadata/identityTypes";
    private static final String ENDPOINT_METADATA_IDENTITY_TYPE_SCHEMA = "/metadata/identityTypes/{identityType}/schema";
    private static final String ENDPOINT_METADATA_IDENTITY_TYPE_ATTRIBUTES = "/metadata/identityTypes/{identityType}/attributes";

    /**
     * Constructs a client with the given dropwizard environment
     */
    public AlchemyClient(AlchemyClientConfiguration configuration, Environment environment) {
        this.client = clientBuilder
            .using(environment)
            .using(configuration)
            .build(CLIENT_NAME);
        this.configuration = configuration;
        configureObjectMapper(configuration, environment.getObjectMapper());
    }

    /**
     * Constructs a client with the given executor service and object mapper
     */
    public AlchemyClient(AlchemyClientConfiguration configuration,
                         ExecutorService executorService,
                         ObjectMapper objectMapper) {
        this.client = clientBuilder
            .using(executorService, objectMapper)
            .using(configuration)
            .build(CLIENT_NAME);
        this.configuration = configuration;
        configureObjectMapper(configuration, objectMapper);
    }

    /**
     * Constructs a client with the given executor service
     */
    public AlchemyClient(AlchemyClientConfiguration configuration,
                         ExecutorService executorService) {
        final ObjectMapper mapper = Jackson.newObjectMapper();
        this.client = clientBuilder
            .using(executorService, mapper)
            .using(configuration)
            .build(CLIENT_NAME);
        this.configuration = configuration;
        configureObjectMapper(configuration, mapper);
    }

    /**
     * Constructs a client with reasonable multi-threading defaults
     */
    public AlchemyClient(AlchemyClientConfiguration configuration) {
        final ObjectMapper mapper = Jackson.newObjectMapper();
        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.client = clientBuilder
            .using(executorService, mapper)
            .using(configuration)
            .build(CLIENT_NAME);
        this.configuration = configuration;
        configureObjectMapper(configuration, mapper);
    }

    private static void configureObjectMapper(AlchemyClientConfiguration configuration, ObjectMapper mapper) {
        mapper.registerSubtypes(
            configuration
                .getIdentityTypes()
                .toArray(new Class<?>[configuration.getIdentityTypes().size()])
        );
    }

    private URI assembleRequestURI(Map<String, ?> pathParams, String path) {
        return
            UriBuilder
                .fromUri(configuration.getService())
                .path(path)
                .buildFromEncodedMap(pathParams);
    }

    protected WebResource.Builder resource(String path, Map<String, ?> pathParams) {
        return
            client
                .resource(assembleRequestURI(pathParams, path))
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE);
    }

    protected WebResource.Builder resource(String url) {
        return resource(url, EMPTY_PARAMS);
    }

    protected static <K, V> GenericType<Map<K, V>> map(Class<K> keyType, Class<V> valueType) {
        return new GenericType<Map<K, V>>(ParameterizedTypeImpl.make(Map.class, new Type[] {keyType, valueType}, null)) {};
    }

    protected static <T> GenericType<List<T>> list(Class<T> elementType) {
        return new GenericType<List<T>>(ParameterizedTypeImpl.make(List.class, new Type[] {elementType}, null)) {};
    }

    protected static <T> GenericType<Set<T>> set(Class<T> elementType) {
        return new GenericType<Set<T>>(ParameterizedTypeImpl.make(Set.class, new Type[] {elementType}, null)) {};
    }

    public List<ExperimentDto> getExperiments() {
        return resource(ENDPOINT_EXPERIMENTS).get(list(ExperimentDto.class));
    }

    public ExperimentDto getExperiment(String experimentName) {
        return resource(
            ENDPOINT_EXPERIMENT,
            ImmutableMap.of(
                PARAM_EXPERIMENT_NAME, experimentName
            )
        ).get(ExperimentDto.class);
    }

    public List<AllocationDto> getAllocations(String experimentName) {
        return
            resource(
                ENDPOINT_ALLOCATIONS,
                ImmutableMap.of(PARAM_EXPERIMENT_NAME, experimentName)
            ).get(list(AllocationDto.class));
    }

    public List<TreatmentDto> getTreatments(String experimentName) {
        return resource(
            ENDPOINT_TREATMENTS,
            ImmutableMap.of(PARAM_EXPERIMENT_NAME, experimentName)
        ).get(list(TreatmentDto.class));
    }

    public TreatmentDto getTreatment(String experimentName, String treatmentName) {
        return resource(
            ENDPOINT_TREATMENT,
            ImmutableMap.of(
                PARAM_EXPERIMENT_NAME, experimentName,
                PARAM_TREATMENT_NAME, treatmentName
            )
        ).get(TreatmentDto.class);
    }

    public List<TreatmentOverrideDto> getOverrides(String experimentName) {
        return resource(
            ENDPOINT_OVERRIDES,
            ImmutableMap.of(
                PARAM_EXPERIMENT_NAME, experimentName
            )
        ).get(list(TreatmentOverrideDto.class));
    }

    public TreatmentOverrideDto getOverride(String experimentName, String overrideName) {
        return resource(
            ENDPOINT_OVERRIDE,
            ImmutableMap.of(
                PARAM_EXPERIMENT_NAME, experimentName,
                PARAM_OVERRIDE_NAME, overrideName
            )
        ).get(TreatmentOverrideDto.class);
    }

    public CreateExperimentRequestBuilder createExperiment(String experimentName) {
        return new CreateExperimentRequestBuilder(experimentName, resource(ENDPOINT_EXPERIMENTS));
    }

    public void addTreatment(String experimentName, String treatmentName, String description) {
        resource(
            ENDPOINT_TREATMENTS,
            ImmutableMap.of(
                PARAM_EXPERIMENT_NAME, experimentName
            )
        ).put(new TreatmentDto(treatmentName, description));
    }

    public void addTreatment(String experimentName, String treatmentName) {
        addTreatment(experimentName, treatmentName, null);
    }

    public void addOverride(String experimentName,
                            String overrideName,
                            String treatmentName,
                            String filter) {
        resource(
            ENDPOINT_OVERRIDES,
            ImmutableMap.of(
                PARAM_EXPERIMENT_NAME, experimentName
            )
        ).put(new TreatmentOverrideRequest(treatmentName, filter, overrideName));
    }

    public UpdateExperimentRequestBuilder updateExperiment(String experimentName) {
        return new UpdateExperimentRequestBuilder(
            resource(
                ENDPOINT_EXPERIMENT,
                ImmutableMap.of(PARAM_EXPERIMENT_NAME, experimentName)
            )
        );
    }

    public UpdateAllocationsRequestBuilder updateAllocations(String experimentName) {
        return new UpdateAllocationsRequestBuilder(
            resource(
                ENDPOINT_ALLOCATIONS,
                ImmutableMap.of(PARAM_EXPERIMENT_NAME, experimentName)
            )
        );
    }

    public TreatmentDto getActiveTreatment(String experimentName, IdentityDto identity) {
        return resource(
            ENDPOINT_ACTIVE_TREATMENT,
            ImmutableMap.of(
                PARAM_EXPERIMENT_NAME, experimentName
            )
        ).post(TreatmentDto.class, identity);
    }

    public Map<String, TreatmentDto> getActiveTreatments(IdentityDto identity) {
        return
            resource(ENDPOINT_ACTIVE_TREATMENTS)
                .post(
                    map(String.class, TreatmentDto.class), identity
                );
    }

    public void deleteExperiment(String experimentName) {
        resource(
            ENDPOINT_EXPERIMENT,
            ImmutableMap.of(
                PARAM_EXPERIMENT_NAME, experimentName
            )
        ).delete();
    }

    public void clearAllocations(String experimentName) {
        resource(
            ENDPOINT_ALLOCATIONS,
            ImmutableMap.of(
                PARAM_EXPERIMENT_NAME, experimentName
            )
        ).delete();
    }

    public void removeTreatment(String experimentName, String treatmentName) {
        resource(
            ENDPOINT_TREATMENT,
            ImmutableMap.of(
                PARAM_EXPERIMENT_NAME, experimentName,
                PARAM_TREATMENT_NAME, treatmentName
            )
        ).delete();
    }

    public void clearTreatments(String experimentName) {
        resource(
            ENDPOINT_TREATMENTS,
            ImmutableMap.of(
                PARAM_EXPERIMENT_NAME, experimentName
            )
        ).delete();
    }

    public void clearOverrides(String experimentName) {
        resource(
            ENDPOINT_OVERRIDES,
            ImmutableMap.of(
                PARAM_EXPERIMENT_NAME, experimentName
            )
        ).delete();
    }

    public void removeOverride(String experimentName, String overrideName) {
        resource(
            ENDPOINT_OVERRIDE,
            ImmutableMap.of(
                PARAM_EXPERIMENT_NAME, experimentName,
                PARAM_OVERRIDE_NAME, overrideName
            )
        ).delete();
    }

    public Map<String, Class<? extends IdentityDto>> getIdentityTypes() {
        final Map<String, Class> map = resource(ENDPOINT_METADATA_IDENTITY_TYPES).get(map(String.class, Class.class));

        return Maps.transformValues(map, CLASS_TYPE_MAPPPER);
    }

    public JsonSchema getIdentitySchema(String identityType) {
        return resource(
            ENDPOINT_METADATA_IDENTITY_TYPE_SCHEMA,
            ImmutableMap.of(PARAM_IDENTITY_TYPE_NAME, identityType)
        ).get(JsonSchema.class);
    }

    public Set<String> getIdentityAttributes(String identityType) {
        return resource(
                ENDPOINT_METADATA_IDENTITY_TYPE_ATTRIBUTES,
                ImmutableMap.of(PARAM_IDENTITY_TYPE_NAME, identityType)
        ).get(set(String.class));
    }

    private static class ClassTypeMappper implements Function<Class, Class<? extends IdentityDto>> {
        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public Class<? extends IdentityDto> apply(@Nullable Class input) {
            return input;
        }
    }
}
