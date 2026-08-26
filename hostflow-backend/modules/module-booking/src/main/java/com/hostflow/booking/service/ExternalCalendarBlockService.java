package com.hostflow.booking.service;

import com.hostflow.booking.entity.ExternalCalendarBlock;
import com.hostflow.booking.repository.ExternalCalendarBlockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * The write side of a sync: given already-parsed VEVENTs (parsing itself
 * lives in app/scheduling.ExternalCalendarSyncJob, which also owns the HTTP
 * fetch -- this module has no HTTP client dependency and shouldn't need
 * one), upsert by externalUid and remove blocks whose VEVENT disappeared
 * from the feed (the OTA un-blocked those dates).
 */
@Service
public class ExternalCalendarBlockService {

    private final ExternalCalendarBlockRepository repository;

    public ExternalCalendarBlockService(ExternalCalendarBlockRepository repository) {
        this.repository = repository;
    }

    public record ParsedEvent(String uid, LocalDate startDate, LocalDate endDate) {
    }

    @Transactional
    public void syncBlocks(UUID linkId, UUID propertyId, List<ParsedEvent> events) {
        for (ParsedEvent event : events) {
            ExternalCalendarBlock block = repository.findByLinkIdAndExternalUid(linkId, event.uid())
                    .orElseGet(() -> new ExternalCalendarBlock(linkId, propertyId, event.uid(), event.startDate(), event.endDate()));
            block.updateRange(event.startDate(), event.endDate());
            repository.save(block);
        }
        List<String> currentUids = events.stream().map(ParsedEvent::uid).toList();
        if (currentUids.isEmpty()) {
            repository.deleteAll(repository.findByLinkId(linkId));
        } else {
            repository.deleteByLinkIdAndExternalUidNotIn(linkId, currentUids);
        }
    }
}
