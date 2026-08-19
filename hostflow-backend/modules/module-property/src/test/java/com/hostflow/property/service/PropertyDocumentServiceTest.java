package com.hostflow.property.service;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.property.dto.PropertyDocumentResponse;
import com.hostflow.property.entity.PropertyDocument;
import com.hostflow.property.repository.PropertyDocumentRepository;
import com.hostflow.property.repository.PropertyRepository;
import com.hostflow.storage.StorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyDocumentServiceTest {

    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private PropertyDocumentRepository documentRepository;
    @Mock
    private StorageService storageService;

    private PropertyDocumentService service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new PropertyDocumentService(propertyRepository, documentRepository, storageService);
    }

    @Test
    void upload_contractAsPdf_isAllowed() {
        UUID propertyId = UUID.randomUUID();
        when(propertyRepository.existsById(propertyId)).thenReturn(true);
        when(documentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(storageService.generatePresignedGetUrl(anyString())).thenReturn("http://signed/contract.pdf");

        MockMultipartFile file = new MockMultipartFile("file", "lease.pdf", "application/pdf", "pdf-bytes".getBytes());

        PropertyDocumentResponse response = service.upload(propertyId, file, PropertyDocument.DocumentType.CONTRACT);

        assertThat(response.documentType()).isEqualTo("CONTRACT");
    }

    @Test
    void upload_photoAsPdf_isRejected() {
        UUID propertyId = UUID.randomUUID();
        when(propertyRepository.existsById(propertyId)).thenReturn(true);
        MockMultipartFile file = new MockMultipartFile("file", "doc.pdf", "application/pdf", "content".getBytes());

        assertThatThrownBy(() -> service.upload(propertyId, file, PropertyDocument.DocumentType.PHOTO))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void upload_insuranceDoc_allowsLarger25MbLimit() {
        UUID propertyId = UUID.randomUUID();
        when(propertyRepository.existsById(propertyId)).thenReturn(true);
        when(documentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(storageService.generatePresignedGetUrl(anyString())).thenReturn("http://signed/insurance.pdf");

        byte[] twentyMb = new byte[20 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile("file", "policy.pdf", "application/pdf", twentyMb);

        PropertyDocumentResponse response = service.upload(propertyId, file, PropertyDocument.DocumentType.INSURANCE);

        assertThat(response).isNotNull();
    }

    @Test
    void listDocuments_filtersToRequestedType() {
        UUID propertyId = UUID.randomUUID();
        PropertyDocument photo = new PropertyDocument(propertyId, "k1", "a.jpg", "image/jpeg", PropertyDocument.DocumentType.PHOTO);
        PropertyDocument contract = new PropertyDocument(propertyId, "k2", "b.pdf", "application/pdf", PropertyDocument.DocumentType.CONTRACT);
        when(documentRepository.findByPropertyId(propertyId)).thenReturn(List.of(photo, contract));
        when(storageService.generatePresignedGetUrl(anyString())).thenReturn("http://signed/x");

        List<PropertyDocumentResponse> results = service.listDocuments(propertyId, PropertyDocument.DocumentType.CONTRACT);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).documentType()).isEqualTo("CONTRACT");
    }

    @Test
    void deleteDocument_rejectsMismatchedPropertyId() {
        UUID propertyId = UUID.randomUUID();
        UUID otherPropertyId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        PropertyDocument document = new PropertyDocument(
                otherPropertyId, "key", "file.jpg", "image/jpeg", PropertyDocument.DocumentType.PHOTO);
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));

        assertThatThrownBy(() -> service.deleteDocument(propertyId, documentId))
                .isInstanceOf(BusinessRuleException.class);
    }
}
