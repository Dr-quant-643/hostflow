package com.hostflow.identity.dto;

import com.hostflow.identity.entity.UserRole;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record UpdateUserRolesRequest(@NotEmpty Set<UserRole> roles) {
}
