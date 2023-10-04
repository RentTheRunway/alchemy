package io.rtr.alchemy.example.mappers;

import io.rtr.alchemy.example.dto.UserDto;
import io.rtr.alchemy.example.identities.User;
import io.rtr.alchemy.mapping.Mapper;

/** Maps to and from UserDto to User */
@SuppressWarnings("unused")
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
