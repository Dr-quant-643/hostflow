package com.hostflow.rental.service;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.rental.entity.RentalInquiry;
import com.hostflow.rental.repository.RentalInquiryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RentalInquiryService {

    private final RentalInquiryRepository rentalInquiryRepository;

    public RentalInquiryService(RentalInquiryRepository rentalInquiryRepository) {
        this.rentalInquiryRepository = rentalInquiryRepository;
    }

    @Transactional
    public RentalInquiry create(UUID propertyId, UUID guestUserId, UUID ownerUserId, String message) {
        return rentalInquiryRepository.save(new RentalInquiry(propertyId, guestUserId, ownerUserId, message));
    }

    @Transactional(readOnly = true)
    public RentalInquiry getById(UUID id) {
        return rentalInquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RentalInquiry", id));
    }

    @Transactional(readOnly = true)
    public Page<RentalInquiry> listByProperty(UUID propertyId, int limit, int offset) {
        return rentalInquiryRepository.findByPropertyId(propertyId, PageRequest.of(offset / Math.max(limit, 1), limit));
    }

    @Transactional
    public RentalInquiry reply(UUID id, String replyMessage) {
        RentalInquiry inquiry = getById(id);
        inquiry.reply(replyMessage);
        return inquiry;
    }
}
