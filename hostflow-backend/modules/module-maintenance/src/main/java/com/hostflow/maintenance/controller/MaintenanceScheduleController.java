package com.hostflow.maintenance.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.maintenance.dto.CreateMaintenanceScheduleRequest;
import com.hostflow.maintenance.entity.MaintenanceSchedule;
import com.hostflow.maintenance.repository.MaintenanceScheduleRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/maintenance/schedules")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class MaintenanceScheduleController {

    private final MaintenanceScheduleRepository repository;

    public MaintenanceScheduleController(MaintenanceScheduleRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<UUID>> create(@Valid @RequestBody CreateMaintenanceScheduleRequest request) {
        MaintenanceSchedule schedule = repository.save(new MaintenanceSchedule(
                request.propertyId(), request.assetId(), request.category(), request.title(),
                request.intervalDays(), request.firstDueDate()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(schedule.getId()));
    }
}
