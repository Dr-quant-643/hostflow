package com.hostflow.app.publicapi;

import com.hostflow.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics/pricing-suggestions")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class PricingSuggestionController {

    private final PricingSuggestionQueries queries;

    public PricingSuggestionController(PricingSuggestionQueries queries) {
        this.queries = queries;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PricingSuggestionQueries.PricingSuggestionRow>>> mine(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.success(
                queries.suggestionsForOwner(UUID.fromString(jwt.getSubject()))));
    }
}
