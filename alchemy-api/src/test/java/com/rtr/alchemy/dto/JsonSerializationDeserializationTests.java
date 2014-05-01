package com.rtr.alchemy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
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
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JsonSerializationDeserializationTests {
    private ObjectMapper mapper;

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new MrBeanModule());
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

        assertEquals(objectTree, jsonTree);
    }

    private void assertJson(Object value) {
        final Class<?> clazz = value.getClass().isAnonymousClass() ? value.getClass().getSuperclass() : value.getClass();
        assertJson(value, clazz.getSimpleName() + ".json");
    }

    private JsonNode readTreeFromResource(String resourceFile) {
        final InputStream stream = JsonSerializationDeserializationTests.class.getClassLoader().getResourceAsStream(resourceFile);
        assertNotNull(String.format("could not load resource file %s", resourceFile), stream);

        try {
            return mapper.readTree(stream);
        } catch (IOException e) {
            throw new AssertionError(
                String.format(
                    "could not parse json from resource file %s: %s",
                    resourceFile,
                    e.getMessage()
                )
            );
        } finally {
            try { stream.close(); } catch (IOException ignored) { }
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
            "my new experiment",
            "user",
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
        assertJson(new AllocateRequest() {
            @Override
            public String getTreatment() {
                return "control";
            }

            @Override
            public Integer getSize() {
                return 10;
            }
        });
    }

    @Test
    public void testAllocationRequest() {
        assertJson(new AllocationRequest.Allocate() {
            @Override
            public String getTreatment() {
                return "control";
            }

            @Override
            public Integer getSize() {
                return 10;
            }
        });

        assertJson(new AllocationRequest.Deallocate() {
            @Override
            public String getTreatment() {
                return "control";
            }

            @Override
            public Integer getSize() {
                return 10;
            }
        });

        assertJson(new AllocationRequest.Reallocate() {
            @Override
            public String getTarget() {
                return "other";
            }

            @Override
            public String getTreatment() {
                return "control";
            }

            @Override
            public Integer getSize() {
                return 10;
            }
        });
    }

    @Test
    public void testCreateExperimentRequest() {
        assertJson(
            new CreateExperimentRequest() {
                @Override
                public String getName() {
                    return "my_experiment";
                }

                @Override
                public String getDescription() {
                    return "my new experiment";
                }

                @Override
                public String getIdentityType() {
                    return "user";
                }

                @Override
                public Boolean isActive() {
                    return true;
                }

                @Override
                public List<TreatmentDto> getTreatments() {
                    return Lists.newArrayList(
                        new TreatmentDto("control", "the base case"),
                        new TreatmentDto("x", "some other condition")
                    );
                }

                @Override
                public List<AllocateRequest> getAllocations() {
                    return Lists.newArrayList(
                        new AllocateRequest() {
                            @Override
                            public String getTreatment() {
                                return "control";
                            }

                            @Override
                            public Integer getSize() {
                                return 5;
                            }
                        },
                        new AllocateRequest() {
                            @Override
                            public String getTreatment() {
                                return "x";
                            }

                            @Override
                            public Integer getSize() {
                                return 10;
                            }
                        }
                    );
                }

                @Override
                public List<TreatmentOverrideRequest> getOverrides() {
                    return Lists.<TreatmentOverrideRequest>newArrayList(
                        new TreatmentOverrideRequest() {
                            @Override
                            public String getTreatment() {
                                return "control";
                            }

                            @Override
                            public IdentityDto getIdentity() {
                                return new MockIdentityDto("foo");
                            }

                            @Override
                            public String getName() {
                                return "qa_override";
                            }
                        }
                    );
                }
            }
        );
    }

    @Test
    public void testTreatmentOverrideRequest() {
        assertJson(
            new TreatmentOverrideRequest() {
                @Override
                public String getTreatment() {
                    return "control";
                }

                @Override
                public IdentityDto getIdentity() {
                    return new MockIdentityDto("foo");
                }

                @Override
                public String getName() {
                    return "qa_override";
                }
            }
        );
    }

    @Test
    public void testUpdateExperimentRequest() {
        assertJson(
            new UpdateExperimentRequest() {
                @Override
                public Optional<String> getDescription() {
                    return Optional.of("my new experiment");
                }

                @Override
                public Optional<String> getIdentityType() {
                    return Optional.of("user");
                }

                @Override
                public Optional<Boolean> getActive() {
                    return Optional.of(true);
                }

                @Override
                public Optional<List<TreatmentDto>> getTreatments() {
                    return Optional.<List<TreatmentDto>>of(
                        Lists.newArrayList(
                            new TreatmentDto("control", "the base case"),
                            new TreatmentDto("x", "some other condition")
                        )
                    );
                }

                @Override
                public Optional<List<AllocateRequest>> getAllocations() {
                    return Optional.<List<AllocateRequest>>of(
                        Lists.newArrayList(
                            new AllocateRequest() {
                                @Override
                                public String getTreatment() {
                                    return "control";
                                }

                                @Override
                                public Integer getSize() {
                                    return 5;
                                }
                            },
                            new AllocateRequest() {
                                @Override
                                public String getTreatment() {
                                    return "x";
                                }

                                @Override
                                public Integer getSize() {
                                    return 10;
                                }
                            }
                        )
                    );
                }

                @Override
                public Optional<List<TreatmentOverrideRequest>> getOverrides() {
                    return Optional.<List<TreatmentOverrideRequest>>of(
                        Lists.<TreatmentOverrideRequest>newArrayList(
                            new TreatmentOverrideRequest() {
                                @Override
                                public String getTreatment() {
                                    return "control";
                                }

                                @Override
                                public IdentityDto getIdentity() {
                                    return new MockIdentityDto("foo");
                                }

                                @Override
                                public String getName() {
                                    return "qa_override";
                                }
                            }
                        )
                    );
                }
            }
        );
    }

}
