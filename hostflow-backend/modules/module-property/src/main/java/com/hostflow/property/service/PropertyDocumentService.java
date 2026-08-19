package com.hostflow.property.service;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.property.dto.PropertyDocumentResponse;
import com.hostflow.property.entity.PropertyDocument;
import com.hostflow.property.repository.PropertyDocumentRepository;
import com.hostflow.property.repository.PropertyRepository;
import com.hostflow.storage.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * UPDATED: document type is now a real parameter, not hardcoded to PHOTO.
 * Allowed content types and max size now vary by document type — images stay
 * tight (10MB, JPEG/PNG/WebP only), while CONTRACT/INSURANCE/OTHER allow PDFs too
 * and a larger 25MB cap, since legal/insurance documents are often scanned PDFs.
 */
@Service
public class PropertyDocumentService {

    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Set<String> DOCUMENT_TYPES_ALLOWED = Set.of(
            "image/jpeg", "image/png", "image/webp", "application/pdf");

    private static final long IMAGE_MAX_SIZE_BYTES = 10L * 1024 * 1024;
    private static final long DOCUMENT_MAX_SIZE_BYTES = 25L * 1024 * 1024;

    private final PropertyRepository propertyRepository;
    private final PropertyDocumentRepository documentRepository;
    private final StorageService storageService;

    public PropertyDocumentService(PropertyRepository propertyRepository,
                                    PropertyDocumentRepository documentRepository,
                                    StorageService storageService) {
        this.propertyRepository = propertyRepository;
        this.documentRepository = documentRepository;
        this.storageService = storageService;
    }

    @Transactional
    public PropertyDocumentResponse upload(UUID propertyId, MultipartFile file, PropertyDocument.DocumentType documentType) {
        if (!propertyRepository.existsById(propertyId)) {
            throw new ResourceNotFoundException("Property", propertyId);
        }
        validateFile(file, documentType);

        String objectKey = buildObjectKey(propertyId, documentType, file.getOriginalFilename());

        try {
            storageService.uploadObject(objectKey, file.getBytes(), file.getContentType());
        } catch (IOException e) {
            throw new BusinessRuleException("Failed to read uploaded file: " + e.getMessage());
        }

        PropertyDocument document = new PropertyDocument(
                propertyId, objectKey, file.getOriginalFilename(), file.getContentType(), documentType);
        document = documentRepository.save(document);

        return PropertyDocumentResponse.from(document, storageService.generatePresignedGetUrl(objectKey));
    }

    @Transactional(readOnly = true)
    public List<PropertyDocumentResponse> listDocuments(UUID propertyId, PropertyDocument.DocumentType filterType) {
        List<PropertyDocument> documents = documentRepository.findByPropertyId(propertyId);
        if (filterType != null) {
            documents = documents.stream().filter(d -> d.getDocumentType() == filterType).toList();
        }
        return documents.stream()
                .map(doc -> PropertyDocumentResponse.from(doc, storageService.generatePresignedGetUrl(doc.getObjectKey())))
                .toList();
    }

    @Transactional
    public void deleteDocument(UUID propertyId, UUID documentId) {
        PropertyDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("PropertyDocument", documentId));

        if (!document.getPropertyId().equals(propertyId)) {
            throw new BusinessRuleException("Document does not belong to the specified property");
        }

        storageService.deleteObject(document.getObjectKey());
        documentRepository.delete(document);
    }

    private void validateFile(MultipartFile file, PropertyDocument.DocumentType documentType) {
        if (file.isEmpty()) {
            throw new BusinessRuleException("Uploaded file is empty");
        }

        boolean isImageOnlyType = documentType == PropertyDocument.DocumentType.PHOTO
                || documentType == PropertyDocument.DocumentType.FLOOR_PLAN;

        long maxSize = isImageOnlyType ? IMAGE_MAX_SIZE_BYTES : DOCUMENT_MAX_SIZE_BYTES;
        Set<String> allowedTypes = isImageOnlyType ? IMAGE_TYPES : DOCUMENT_TYPES_ALLOWED;

        if (file.getSize() > maxSize) {
            throw new BusinessRuleException("File exceeds the " + (maxSize / (1024 * 1024)) + "MB size limit");
        }
        if (file.getContentType() == null || !allowedTypes.contains(file.getContentType())) {
            throw new BusinessRuleException("File type not allowed for " + documentType +
                    " — allowed: " + allowedTypes);
        }
    }

    private String buildObjectKey(UUID propertyId, PropertyDocument.DocumentType type, String originalFilename) {
        String safeFilename = originalFilename == null ? "upload" : originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        return "properties/" + propertyId + "/" + type.name().toLowerCase() + "/" + UUID.randomUUID() + "-" + safeFilename;
    }
}
