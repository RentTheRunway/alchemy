package io.rtr.alchemy.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.client.JerseyClientConfiguration;
import io.rtr.alchemy.dto.identities.IdentityDto;

import java.net.URI;
import java.util.Set;

import javax.validation.constraints.NotNull;

/** The client configuration */
public class AlchemyClientConfiguration extends JerseyClientConfiguration {
    @NotNull private final URI service;

    @NotNull private final Set<Class<? extends IdentityDto>> identityTypes;

    @JsonCreator
    public AlchemyClientConfiguration(
            @JsonProperty("service") URI service,
            @JsonProperty("identityTypes") Set<Class<? extends IdentityDto>> identityTypes) {
        this.service = service;
        this.identityTypes = identityTypes;
    }

    public URI getService() {
        return service;
    }

    public Set<Class<? extends IdentityDto>> getIdentityTypes() {
        return identityTypes;
    }
}
