package com.rtr.alchemy.service.identities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.rtr.alchemy.identities.Identity;

@JsonTypeName("geoLocation")
public class GeoLocation extends Identity {
    private String country;
    private String region;
    private String city;
    private String postalCode;
    private Float latitude;
    private Float longitude;
    private String metroCode;
    private String areaCode;

    @JsonCreator
    public GeoLocation(@JsonProperty("country") String country,
                       @JsonProperty("region") String region,
                       @JsonProperty("city") String city,
                       @JsonProperty("postalCode") String postalCode,
                       @JsonProperty("latitude") Float latitude,
                       @JsonProperty("longitude") Float longitude,
                       @JsonProperty("metroCode") String metroCode,
                       @JsonProperty("areaCode") String areaCode) {
        this.country = country;
        this.region = region;
        this.city = city;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.metroCode = metroCode;
        this.areaCode = areaCode;
    }

    @Override
    public long getHash(int seed) {
        return
            identity(seed)
                .putString(country)
                .putString(region)
                .putString(city)
                .putString(postalCode)
                .putFloat(latitude)
                .putFloat(longitude)
                .putString(metroCode)
                .putString(areaCode)
                .hash();
    }
}
