package com.hostflow.crm.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.crm.dto.ContactResponse;
import com.hostflow.crm.dto.CreateContactRequest;
import com.hostflow.crm.dto.InteractionResponse;
import com.hostflow.crm.dto.LogInteractionRequest;
import com.hostflow.crm.entity.Contact;
import com.hostflow.crm.entity.ContactStatus;
import com.hostflow.crm.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/crm/contacts")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ContactResponse>>> list(
            @RequestParam(required = false) ContactStatus status,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        List<ContactResponse> contacts = contactService.list(status, limit, offset).stream()
                .map(ContactResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.success(contacts));
    }

    @GetMapping("/{id}/interactions")
    public ResponseEntity<ApiResponse<List<InteractionResponse>>> listInteractions(@PathVariable UUID id) {
        List<InteractionResponse> interactions = contactService.listInteractions(id).stream()
                .map(InteractionResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.success(interactions));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ContactResponse>> create(@Valid @RequestBody CreateContactRequest request) {
        Contact contact = contactService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(ContactResponse.from(contact)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContactResponse>> getById(@PathVariable UUID id) {
        Contact contact = contactService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(ContactResponse.from(contact)));
    }

    @PatchMapping("/{id}/qualify")
    public ResponseEntity<ApiResponse<ContactResponse>> qualify(@PathVariable UUID id) {
        Contact contact = contactService.qualify(id);
        return ResponseEntity.ok(ApiResponse.success(ContactResponse.from(contact)));
    }

    @PostMapping("/{id}/interactions")
    public ResponseEntity<ApiResponse<Void>> logInteraction(@PathVariable UUID id,
                                                              @Valid @RequestBody LogInteractionRequest request,
                                                              @AuthenticationPrincipal Jwt jwt) {
        UUID loggedByUserId = UUID.fromString(jwt.getSubject());
        contactService.logInteraction(id, loggedByUserId, request.type(), request.notes());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null));
    }
}
