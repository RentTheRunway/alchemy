package com.rtr.alchemy.mapping;

import com.rtr.alchemy.identities.Device;
import com.rtr.alchemy.identities.GeoLocation;
import com.rtr.alchemy.identities.Identity;
import com.rtr.alchemy.identities.User;
import com.rtr.alchemy.dto.identities.DeviceDto;
import com.rtr.alchemy.dto.identities.GeoLocationDto;
import com.rtr.alchemy.dto.identities.IdentityDto;
import com.rtr.alchemy.dto.identities.UserDto;
import org.junit.Test;

public class IdentityMappingTest extends MappingTestBase {
    @Test
    public void testUserMapping() {
        register(UserDto.class, User.class);
        configure();
        testMapping(new UserDto(0L), User.class, Identity.class);
        testMapping(new User(0L), UserDto.class, IdentityDto.class);
    }

    @Test
    public void testDeviceMapping() {
        register(DeviceDto.class, Device.class);
        configure();
        testMapping(new DeviceDto("abc"), Device.class, Identity.class);
        testMapping(new Device("abc"), DeviceDto.class, IdentityDto.class);
    }

    @Test
    public void testGeoLocationMapping() {
        register(GeoLocationDto.class, GeoLocation.class);
        configure();
        testMapping(new GeoLocationDto(null, null, null, null, null, null, null, null), GeoLocation.class, Identity.class);
        testMapping(new GeoLocation(null, null, null, null, null, null, null, null), GeoLocationDto.class, IdentityDto.class);
    }
}
