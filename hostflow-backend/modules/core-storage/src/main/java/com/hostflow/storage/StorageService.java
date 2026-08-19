package com.hostflow.storage;

import com.hostflow.storage.config.StorageProperties;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

/**
 * The single point of contact for object storage across the whole codebase. Every
 * module needing file storage (module-property's photos now, module-marketing's
 * generated media later) depends on this rather than talking to S3Client directly
 * — keeps bucket/credential/presign-duration logic in one place, matching the
 * "provider-agnostic, swap without touching call sites" architecture decision.
 *
 * Callers store ONLY the objectKey (never a raw URL) — matches PropertyDocument's
 * existing javadoc, written before this module existed, describing exactly this
 * pattern.
 */
@Service
public class StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final StorageProperties properties;

    public StorageService(S3Client s3Client, S3Presigner s3Presigner, StorageProperties properties) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.properties = properties;
    }

    public void uploadObject(String objectKey, byte[] content, String contentType) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(objectKey)
                    .contentType(contentType)
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(content));
        } catch (Exception e) {
            throw new StorageException("Failed to upload object with key " + objectKey, e);
        }
    }

    /**
     * Generates a time-limited signed URL (expiry from
     * hostflow.storage.presigned-url-expiry-seconds, default 1 hour) rather than
     * exposing a permanent public URL — keeps property photos/documents access-
     * controlled at the point of generation (this method is only ever called from
     * an authorized or explicitly-public-property-scoped code path), not by making
     * the underlying bucket world-readable.
     */
    public String generatePresignedGetUrl(String objectKey) {
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(objectKey)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(properties.getPresignedUrlExpirySeconds()))
                    .getObjectRequest(getRequest)
                    .build();

            return s3Presigner.presignGetObject(presignRequest).url().toString();
        } catch (Exception e) {
            throw new StorageException("Failed to generate presigned URL for key " + objectKey, e);
        }
    }

    public void deleteObject(String objectKey) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(objectKey)
                    .build());
        } catch (Exception e) {
            throw new StorageException("Failed to delete object with key " + objectKey, e);
        }
    }
}
