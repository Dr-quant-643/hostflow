package com.hostflow.analytics.controller;

import com.hostflow.analytics.dto.MonthlyRevenueResponse;
import com.hostflow.analytics.dto.PropertyOccupancyResponse;
import com.hostflow.analytics.service.AnalyticsService;
import com.hostflow.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/property-occupancy")
    public ResponseEntity<ApiResponse<List<PropertyOccupancyResponse>>> propertyOccupancy() {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getPropertyOccupancy()));
    }

    @GetMapping("/monthly-revenue")
    public ResponseEntity<ApiResponse<List<MonthlyRevenueResponse>>> monthlyRevenue() {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getMonthlyRevenue()));
    }
}
