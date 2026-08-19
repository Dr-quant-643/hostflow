package com.hostflow.property.dto;

import com.hostflow.property.entity.PropertyDocument;

import java.util.UUID;

public record PropertyDocumentResponse(UUID id, String fileName, String contentType, String documentType, String url) {

    public static PropertyDocumentResponse from(PropertyDocument document, String signedUrl) {
        return new PropertyDocumentResponse(
                document.getId(), document.getFileName(), document.getContentType(),
                document.getDocumentType().name(), signedUrl);
    }
}
