package com.hostflow.identity.dto;

import com.hostflow.identity.entity.User;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserResponse(UUID id, String email, String firstName, String lastName,
                            Set<String> roles, boolean active) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
                user.isActive()
        );
    }
}
