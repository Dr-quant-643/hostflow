package com.hostflow.property.entity;

import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Stores only the OBJECT KEY (S3/MinIO path), never a raw public URL — per the
 * architecture decision that uploads are provider-agnostic and go through the Spring
 * Boot backend, not accessed directly by clients. A future module-storage (or a
 * shared utility in core-common) will generate short-lived signed URLs from this
 * objectKey on read, keeping the actual storage provider swappable.
 */
@Entity
@Table(name = "property_documents")
public class PropertyDocument extends TenantScopedEntity {

    @Column(name = "property_id", nullable = false)
    private UUID propertyId;

    @Column(name = "object_key", nullable = false)
    private String objectKey;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;

    protected PropertyDocument() {
    }

    public PropertyDocument(UUID propertyId, String objectKey, String fileName,
                             String contentType, DocumentType documentType) {
        this.propertyId = propertyId;
        this.objectKey = objectKey;
        this.fileName = fileName;
        this.contentType = contentType;
        this.documentType = documentType;
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public enum DocumentType {
        PHOTO, VIDEO, FLOOR_PLAN, CONTRACT, INSURANCE, OTHER
    }
}
