package com.hostflow.property.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.property.dto.PropertyDocumentResponse;
import com.hostflow.property.entity.PropertyDocument;
import com.hostflow.property.service.PropertyDocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/properties/{propertyId}/documents")
public class PropertyDocumentController {

    private final PropertyDocumentService documentService;

    public PropertyDocumentController(PropertyDocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
    public ResponseEntity<ApiResponse<PropertyDocumentResponse>> upload(
            @PathVariable UUID propertyId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") PropertyDocument.DocumentType documentType) {
        PropertyDocumentResponse response = documentService.upload(propertyId, file, documentType);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<PropertyDocumentResponse>>> list(
            @PathVariable UUID propertyId,
            @RequestParam(required = false) PropertyDocument.DocumentType documentType) {
        return ResponseEntity.ok(ApiResponse.success(documentService.listDocuments(propertyId, documentType)));
    }

    @DeleteMapping("/{documentId}")
    @PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID propertyId, @PathVariable UUID documentId) {
        documentService.deleteDocument(propertyId, documentId);
        return ResponseEntity.noContent().build();
    }
}
