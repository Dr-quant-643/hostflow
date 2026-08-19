package com.hostflow.mall.service;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.mall.dto.ParkingEntryRequest;
import com.hostflow.mall.entity.ParkingSession;
import com.hostflow.mall.entity.ParkingSessionStatus;
import com.hostflow.mall.repository.ParkingSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ParkingService {

    private static final BigDecimal DEFAULT_HOURLY_RATE = BigDecimal.valueOf(2.00);

    private final ParkingSessionRepository repository;

    public ParkingService(ParkingSessionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ParkingSession enter(ParkingEntryRequest request) {
        boolean alreadyParked = repository.findByPropertyIdAndVehiclePlateAndStatus(
                request.propertyId(), request.vehiclePlate(), ParkingSessionStatus.ACTIVE).isPresent();
        if (alreadyParked) {
            throw new BusinessRuleException("This vehicle already has an active parking session");
        }
        return repository.save(new ParkingSession(request.propertyId(), request.vehiclePlate()));
    }

    @Transactional
    public ParkingSession exit(UUID sessionId) {
        ParkingSession session = repository.findById(sessionId)
                .orElseThrow(() -> new com.hostflow.common.exception.ResourceNotFoundException("ParkingSession", sessionId));
        session.exit(DEFAULT_HOURLY_RATE);
        return session;
    }
}
