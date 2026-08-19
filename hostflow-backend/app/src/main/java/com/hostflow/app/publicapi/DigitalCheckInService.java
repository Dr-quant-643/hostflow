package com.hostflow.app.publicapi;

import com.hostflow.booking.entity.DigitalCheckIn;
import com.hostflow.booking.repository.DigitalCheckInRepository;
import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.storage.StorageService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
public class DigitalCheckInService {

    private final DigitalCheckInRepository repository;
    private final StorageService storageService;

    public DigitalCheckInService(DigitalCheckInRepository repository, StorageService storageService) {
        this.repository = repository;
        this.storageService = storageService;
    }

    @Transactional
    public DigitalCheckIn confirm(UUID bookingId, MultipartFile idDocument) {
        String objectKey = "bookings/" + bookingId + "/id-verification/" + UUID.randomUUID();
        try {
            storageService.uploadObject(objectKey, idDocument.getBytes(), idDocument.getContentType());
        } catch (IOException e) {
            throw new BusinessRuleException("Failed to upload ID document: " + e.getMessage());
        }
        return repository.save(new DigitalCheckIn(bookingId, objectKey));
    }
}
