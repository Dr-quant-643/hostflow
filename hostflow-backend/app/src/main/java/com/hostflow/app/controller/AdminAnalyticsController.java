package com.hostflow.app.controller;

import com.hostflow.app.scheduling.AnalyticsRefreshJob;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
public class AdminAnalyticsController {

    private final AnalyticsRefreshJob analyticsRefreshJob;

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshViews() {
        analyticsRefreshJob.refreshManually();
        return ResponseEntity.ok("Analytics views refresh triggered");
    }
}
