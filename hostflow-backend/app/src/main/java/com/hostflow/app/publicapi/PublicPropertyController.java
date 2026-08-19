package com.hostflow.app.publicapi;

import com.hostflow.common.response.ApiResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
public class PublicPropertyController {

    private final PublicPropertyQueries propertyQueries;
    private final PublicAvailabilityQueries availabilityQueries;
    private final PublicPropertyPhotoQueries photoQueries;

    public PublicPropertyController(PublicPropertyQueries propertyQueries,
            PublicAvailabilityQueries availabilityQueries,
            PublicPropertyPhotoQueries photoQueries) {
        this.propertyQueries = propertyQueries;
        this.availabilityQueries = availabilityQueries;
        this.photoQueries = photoQueries;
    }

    @GetMapping("/api/v1/properties/public")
    public ResponseEntity<ApiResponse<List<PublicPropertyQueries.PublicPropertyRow>>> list(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(ApiResponse.success(propertyQueries.list(limit, offset)));
    }

    @GetMapping("/api/v1/properties/public/search")
    public ResponseEntity<ApiResponse<List<PublicPropertyQueries.PublicPropertyRow>>> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(ApiResponse.success(
                propertyQueries.search(city, propertyType, minPrice, maxPrice, limit, offset)));
    }

    @GetMapping("/api/v1/properties/public/{id}")
    public ResponseEntity<ApiResponse<PublicPropertyQueries.PublicPropertyRow>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(propertyQueries.findPublicById(id)));
    }

    @GetMapping("/api/v1/properties/public/{id}/photos")
    public ResponseEntity<ApiResponse<List<String>>> photos(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(photoQueries.listPhotoUrls(id)));
    }

    @GetMapping("/api/v1/bookings/public/availability")
    public ResponseEntity<ApiResponse<Boolean>> availability(
            @RequestParam UUID propertyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        return ResponseEntity.ok(ApiResponse.success(availabilityQueries.isAvailable(propertyId, checkIn, checkOut)));
    }
}
