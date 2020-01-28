package io.rtr.alchemy.client;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import io.rtr.alchemy.client.builder.CreateExperimentRequestBuilder;
import io.rtr.alchemy.client.builder.GetExperimentsRequestBuilder;
import io.rtr.alchemy.client.builder.UpdateAllocationsRequestBuilder;
import io.rtr.alchemy.client.builder.UpdateExperimentRequestBuilder;
import io.rtr.alchemy.client.builder.UpdateTreatmentRequestBuilder;
import io.rtr.alchemy.dto.identities.IdentityDto;
import io.rtr.alchemy.dto.models.AllocationDto;
import io.rtr.alchemy.dto.models.ExperimentDto;
import io.rtr.alchemy.dto.models.TreatmentDto;
import io.rtr.alchemy.dto.models.TreatmentOverrideDto;
import io.rtr.alchemy.dto.requests.GetExperimentsRequest;
import io.rtr.alchemy.dto.requests.TreatmentOverrideRequest;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.reflection.ParameterizedTypeImpl;

import javax.annotation.Nullable;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
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
    private static final Map<String, ?> EMPTY_PATH_PARAMS = Maps.newHashMap();
    private static final ListMultimap<String, Object> EMPTY_QUERY_PARAMS = ArrayListMultimap.create();
    private static final ClassTypeMapper CLASS_TYPE_MAPPER = new ClassTypeMapper();
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

    private final Function<GetExperimentsRequest, List<ExperimentDto>> GET_EXPERIMENTS_REQUEST_BUILDER =
        new Function<GetExperimentsRequest, List<ExperimentDto>>() {
            @Nullable
            @Override
            public List<ExperimentDto> apply(@Nullable GetExperimentsRequest request) {
                if (request == null) {
                    return null;
                }

                final ListMultimap<String, Object> queryParams = ArrayListMultimap.create();

                if (request.getFilter() != null) {
                    queryParams.put("filter", request.getFilter());
                }

                if (request.getOffset() != null) {
                    queryParams.put("offset", request.getOffset());
                }

                if (request.getLimit() != null) {
                    queryParams.put("limit", request.getLimit());
                }

                if (request.getSort() != null) {
                    queryParams.put("sort", request.getSort());
                }

                final Invocation.Builder builder = resource(ENDPOINT_EXPERIMENTS, queryParams);
                return builder.get(list(ExperimentDto.class));
            }
        };

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

    private URI assembleRequestURI(Map<String, ?> pathParams, ListMultimap<String, Object> queryParams, String path) {
        final UriBuilder builder =
            UriBuilder
                .fromUri(configuration.getService())
                .path(path);

        for (String queryParam : queryParams.keySet()) {
            final List<Object> values = queryParams.get(queryParam);

            for (Object value : values) {
                builder.queryParam(queryParam, value);
            }
        }

        return builder.buildFromEncodedMap(pathParams);
    }

    protected Invocation.Builder resource(String path, Map<String, ?> pathParams) {
        return
            client
                .target(assembleRequestURI(pathParams, EMPTY_QUERY_PARAMS, path))
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE);
    }

    protected Invocation.Builder resource(String path, ListMultimap<String, Object> queryParams) {
        return
            client
                .target(assembleRequestURI(EMPTY_PATH_PARAMS, queryParams, path))
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE);
    }

    protected Invocation.Builder resource(String path, Map<String, ?> pathParams, ListMultimap<String, Object> queryParams) {
        return
            client
                .target(assembleRequestURI(pathParams, queryParams, path))
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE);
    }

    protected Invocation.Builder resource(String url) {
        return resource(url, EMPTY_PATH_PARAMS, EMPTY_QUERY_PARAMS);
    }

    protected static <K, V> GenericType<Map<K, V>> map(Class<K> keyType, Class<V> valueType) {
        return new GenericType<>(new ParameterizedTypeImpl(Map.class, keyType, valueType) {
        });
    }

    protected static <T> GenericType<List<T>> list(Class<T> elementType) {
        return new GenericType<>(new ParameterizedTypeImpl(List.class, elementType) {
        });
    }

    protected static <T> GenericType<Set<T>> set(Class<T> elementType) {
        return new GenericType<>(new ParameterizedTypeImpl(Set.class, elementType) {
        });
    }

    public List<ExperimentDto> getExperiments() {
        return resource(ENDPOINT_EXPERIMENTS).get(list(ExperimentDto.class));
    }

    public GetExperimentsRequestBuilder getExperimentsFiltered() {
        return new GetExperimentsRequestBuilder(GET_EXPERIMENTS_REQUEST_BUILDER);
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
        ).put(Entity.json(new TreatmentDto(treatmentName, description)));
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
        ).put(Entity.entity(
            new TreatmentOverrideRequest(treatmentName, filter, overrideName),
            MediaType.APPLICATION_JSON_TYPE)
        );
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
        ).post(
            Entity.entity(identity, MediaType.APPLICATION_JSON_TYPE),
            TreatmentDto.class);
    }

    public Map<String, TreatmentDto> getActiveTreatments(IdentityDto identity) {
        return
            resource(ENDPOINT_ACTIVE_TREATMENTS)
                .post(
                    Entity.entity(identity, MediaType.APPLICATION_JSON_TYPE),
                    map(String.class, TreatmentDto.class));
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

    public UpdateTreatmentRequestBuilder updateTreatment(String experimentName, String treatmentName) {
        return new UpdateTreatmentRequestBuilder(
            resource(
                ENDPOINT_TREATMENT,
                ImmutableMap.of(
                    PARAM_EXPERIMENT_NAME, experimentName,
                    PARAM_TREATMENT_NAME, treatmentName
                )
            )
        );
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

        return Maps.transformValues(map, CLASS_TYPE_MAPPER);
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

    private static class ClassTypeMapper implements Function<Class, Class<? extends IdentityDto>> {
        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public Class<? extends IdentityDto> apply(@Nullable Class input) {
            return input;
        }
    }
}
