package com.rtr.alchemy.mapping;

import com.rtr.alchemy.identities.Device;
import com.rtr.alchemy.identities.GeoLocation;
import com.rtr.alchemy.identities.User;
import com.rtr.alchemy.dto.identities.DeviceDto;
import com.rtr.alchemy.dto.identities.GeoLocationDto;
import com.rtr.alchemy.dto.identities.UserDto;
import org.junit.Test;

public class IdentityMappingTest extends MappingTestBase {
    @Test
    public void testUserMapping() {
        register(UserDto.class, User.class);
        configure();
        testMapping(UserDto.class, User.class);
    }

    @Test
    public void testDeviceMapping() {
        register(DeviceDto.class, Device.class);
        configure();
        testMapping(DeviceDto.class, Device.class);
    }

    @Test
    public void testGeoLocationMapping() {
        register(GeoLocationDto.class, GeoLocation.class);
        configure();
        testMapping(GeoLocationDto.class, GeoLocation.class);
    }
}
