package com.hostflow.office.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.office.dto.CreateMeetingRoomRequest;
import com.hostflow.office.dto.MeetingRoomResponse;
import com.hostflow.office.entity.MeetingRoom;
import com.hostflow.office.repository.MeetingRoomRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/office/rooms")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class MeetingRoomController {

    private final MeetingRoomRepository roomRepository;

    public MeetingRoomController(MeetingRoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<MeetingRoomResponse>> create(@Valid @RequestBody CreateMeetingRoomRequest request) {
        MeetingRoom room = roomRepository.save(new MeetingRoom(request.propertyId(), request.name(), request.capacity()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(MeetingRoomResponse.from(room)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MeetingRoomResponse>>> listByProperty(@RequestParam UUID propertyId) {
        List<MeetingRoomResponse> rooms = roomRepository.findByPropertyIdAndActiveTrue(propertyId).stream()
                .map(MeetingRoomResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }

    /** Tenant-wide (all properties) -- backs the Dashboard's Office tile,
     *  same reasoning as LeaseController.statusCount / WorkOrderController.openCount. */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> count() {
        return ResponseEntity.ok(ApiResponse.success(roomRepository.countByActiveTrue()));
    }
}
