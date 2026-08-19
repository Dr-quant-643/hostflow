package com.hostflow.mall.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.mall.dto.CreateMallEventRequest;
import com.hostflow.mall.dto.MallEventResponse;
import com.hostflow.mall.entity.MallEvent;
import com.hostflow.mall.repository.MallEventRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mall/events")
public class MallEventController {

    private final MallEventRepository repository;

    public MallEventController(MallEventRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
    @Transactional
    public ResponseEntity<ApiResponse<MallEventResponse>> create(@Valid @RequestBody CreateMallEventRequest request) {
        MallEvent event = repository.save(new MallEvent(request.propertyId(), request.title(),
                request.description(), request.startsAt(), request.endsAt()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(MallEventResponse.from(event)));
    }

    /** Publicly readable — mall events are marketing content, matching the vision
     * doc's "Mall Experience: Events" being part of the NazilCo-facing guest side. */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MallEventResponse>>> listByProperty(@RequestParam UUID propertyId) {
        List<MallEventResponse> events = repository.findByPropertyId(propertyId).stream()
                .map(MallEventResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.success(events));
    }
}
