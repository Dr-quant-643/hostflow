package com.hostflow.app.publicapi;

import com.hostflow.review.entity.Review;
import com.hostflow.review.repository.ReviewRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** Separate bean per the REQUIRES_NEW-self-invocation lesson — called from
 * GuestReviewOrchestrator via constructor injection, never self-invoked. */
@Component
public class GuestReviewWriter {

    private final ReviewRepository reviewRepository;

    public GuestReviewWriter(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public Review create(UUID propertyId, UUID bookingId, UUID reviewerUserId, Integer rating, String comment) {
        return reviewRepository.save(new Review(propertyId, bookingId, reviewerUserId, rating, comment));
    }
}
