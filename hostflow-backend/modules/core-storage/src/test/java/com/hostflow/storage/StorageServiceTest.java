package com.hostflow.storage;

import com.hostflow.storage.config.StorageProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    private S3Client s3Client;
    @Mock
    private S3Presigner s3Presigner;
    @Mock
    private PresignedGetObjectRequest presignedRequest;

    private StorageService storageService;
    private StorageProperties properties;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        properties = new StorageProperties();
        properties.setBucket("hostflow-test-bucket");
        properties.setPresignedUrlExpirySeconds(3600);
        storageService = new StorageService(s3Client, s3Presigner, properties);
    }

    @Test
    void uploadObject_callsPutObjectWithCorrectBucketAndKey() {
        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);

        storageService.uploadObject("properties/prop-1/photo.jpg", "fake-image-bytes".getBytes(), "image/jpeg");

        verify(s3Client).putObject(captor.capture(), any(RequestBody.class));
        assertThat(captor.getValue().bucket()).isEqualTo("hostflow-test-bucket");
        assertThat(captor.getValue().key()).isEqualTo("properties/prop-1/photo.jpg");
        assertThat(captor.getValue().contentType()).isEqualTo("image/jpeg");
    }

    @Test
    void uploadObject_wrapsS3ExceptionInStorageException() {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(new RuntimeException("S3 connection refused"));

        assertThatThrownBy(() -> storageService.uploadObject("key", new byte[0], "image/jpeg"))
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("key");
    }

    @Test
    void generatePresignedGetUrl_returnsUrlFromPresigner() throws MalformedURLException {
        URL fakeUrl = new URL("http://localhost:9000/hostflow-test-bucket/properties/prop-1/photo.jpg?X-Amz-Signature=abc");
        when(presignedRequest.url()).thenReturn(fakeUrl);
        when(s3Presigner.presignGetObject(any(software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest.class)))
                .thenReturn(presignedRequest);

        String result = storageService.generatePresignedGetUrl("properties/prop-1/photo.jpg");

        assertThat(result).contains("properties/prop-1/photo.jpg");
    }

    @Test
    void deleteObject_callsDeleteObjectWithCorrectKey() {
        ArgumentCaptor<DeleteObjectRequest> captor = ArgumentCaptor.forClass(DeleteObjectRequest.class);

        storageService.deleteObject("properties/prop-1/photo.jpg");

        verify(s3Client).deleteObject(captor.capture());
        assertThat(captor.getValue().key()).isEqualTo("properties/prop-1/photo.jpg");
        assertThat(captor.getValue().bucket()).isEqualTo("hostflow-test-bucket");
    }
}
