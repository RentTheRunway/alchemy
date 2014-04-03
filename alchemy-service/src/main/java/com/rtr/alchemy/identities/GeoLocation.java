package com.rtr.alchemy.identities;

/**
 * Represents a specific location on Earth
 */
public class GeoLocation extends Identity {
    private final String country;
    private final String region;
    private final String city;
    private final String postalCode;
    private final Float latitude;
    private final Float longitude;
    private final String metroCode;
    private final String areaCode;

    public GeoLocation(String country,
                       String region,
                       String city,
                       String postalCode,
                       Float latitude,
                       Float longitude,
                       String metroCode,
                       String areaCode) {
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
