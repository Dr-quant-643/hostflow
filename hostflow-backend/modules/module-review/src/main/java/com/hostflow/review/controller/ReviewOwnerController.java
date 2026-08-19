package com.hostflow.review.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.review.dto.OwnerResponseRequest;
import com.hostflow.review.dto.ReviewResponse;
import com.hostflow.review.entity.Review;
import com.hostflow.review.service.ReviewOwnerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewOwnerController {

    private final ReviewOwnerService service;

    public ReviewOwnerController(ReviewOwnerService service) {
        this.service = service;
    }

    /** Publicly readable — reviews are guest-decision-making content, matching how
     * properties/photos are public. */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> listByProperty(
            @RequestParam UUID propertyId,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(ApiResponse.success(service.listByProperty(propertyId, limit, offset).map(ReviewResponse::from)));
    }

    @PatchMapping("/{id}/respond")
    @PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
    public ResponseEntity<ApiResponse<ReviewResponse>> respond(@PathVariable UUID id, @Valid @RequestBody OwnerResponseRequest request) {
        Review review = service.respond(id, request.response());
        return ResponseEntity.ok(ApiResponse.success(ReviewResponse.from(review)));
    }
}
