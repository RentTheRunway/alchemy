package com.rtr.alchemy.client.mappers;

import com.rtr.alchemy.client.dto.UserDto;
import com.rtr.alchemy.client.identities.User;
import com.rtr.alchemy.mapping.Mapper;

public class UserMapper implements Mapper<UserDto, User> {
    @Override
    public UserDto toDto(User source) {
        return new UserDto(source.getName());
    }

    @Override
    public User fromDto(UserDto source) {
        return new User(source.getName());
    }
}
