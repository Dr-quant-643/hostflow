package com.hostflow.office.dto;

import com.hostflow.office.entity.Visitor;

import java.time.Instant;
import java.util.UUID;

public record VisitorResponse(UUID id, String fullName, String company, Instant expectedAt,
                               Instant checkedInAt, Instant checkedOutAt, String status) {
    public static VisitorResponse from(Visitor v) {
        return new VisitorResponse(v.getId(), v.getFullName(), v.getCompany(), v.getExpectedAt(),
                v.getCheckedInAt(), v.getCheckedOutAt(), v.getStatus().name());
    }
}
