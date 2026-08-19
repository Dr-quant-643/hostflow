package com.hostflow.identity.dto;

import com.hostflow.identity.entity.GuestProfile;

import java.util.UUID;

public record GuestProfileResponse(UUID id, String email, String firstName, String lastName) {

    public static GuestProfileResponse from(GuestProfile profile) {
        return new GuestProfileResponse(profile.getId(), profile.getEmail(), profile.getFirstName(),
                profile.getLastName());
    }
}
