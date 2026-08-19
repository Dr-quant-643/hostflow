package com.hostflow.review.entity;

import com.hostflow.common.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReviewEntityTest {

    @Test
    void constructor_rejectsRatingBelowOne() {
        assertThatThrownBy(() -> new Review(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 0, "Bad"))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void constructor_rejectsRatingAboveFive() {
        assertThatThrownBy(() -> new Review(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 6, "Too good"))
                .isInstanceOf(BusinessRuleException.class);
    }
}
