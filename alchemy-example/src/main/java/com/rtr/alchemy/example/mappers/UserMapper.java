package com.rtr.alchemy.example.mappers;

import com.rtr.alchemy.example.dto.UserDto;
import com.rtr.alchemy.example.identities.User;
import com.rtr.alchemy.mapping.Mapper;

public class UserMapper implements Mapper<UserDto, User> {
    @Override
    public User fromDto(UserDto source) {
        return new User(source.getName());
    }

    @Override
    public UserDto toDto(User source) {
        return new UserDto(source.getName());
    }
}
