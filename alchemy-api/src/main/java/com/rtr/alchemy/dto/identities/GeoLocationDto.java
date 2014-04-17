package com.rtr.alchemy.dto.identities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("geoLocation")
public class GeoLocationDto extends IdentityDto {
    private final String country;
    private final String region;
    private final String city;
    private final String postalCode;
    private final Float latitude;
    private final Float longitude;
    private final String metroCode;
    private final String areaCode;

    @JsonCreator
    public GeoLocationDto(@JsonProperty("country") String country,
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

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public Float getLatitude() {
        return latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public String getMetroCode() {
        return metroCode;
    }

    public String getAreaCode() {
        return areaCode;
    }
}
