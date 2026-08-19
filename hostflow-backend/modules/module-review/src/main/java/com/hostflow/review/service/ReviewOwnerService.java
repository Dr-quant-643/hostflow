package com.hostflow.review.service;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.review.entity.Review;
import com.hostflow.review.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * XanuOS-side, RLS-scoped access — owners viewing/responding to reviews of their
 * OWN properties (tenant context comes naturally from their JWT). The
 * review-CREATION path (guest submitting a review) lives in app, since it needs
 * the cross-tenant property-lookup-then-set-context pattern established by
 * GuestBookingOrchestrator.
 */
@Service
public class ReviewOwnerService {

    private final ReviewRepository reviewRepository;

    public ReviewOwnerService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional(readOnly = true)
    public Page<Review> listByProperty(UUID propertyId, int limit, int offset) {
        return reviewRepository.findByPropertyId(propertyId, PageRequest.of(offset / Math.max(limit, 1), limit));
    }

    @Transactional
    public Review respond(UUID reviewId, String response) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", reviewId));
        review.addOwnerResponse(response);
        return review;
    }
}