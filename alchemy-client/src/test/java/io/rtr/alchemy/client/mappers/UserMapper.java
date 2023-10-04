package io.rtr.alchemy.client.mappers;

import io.rtr.alchemy.client.dto.UserDto;
import io.rtr.alchemy.client.identities.User;
import io.rtr.alchemy.mapping.Mapper;

// referenced in test-server.yaml
@SuppressWarnings("unused")
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
