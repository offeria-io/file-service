package offeria.file_service.service.impl;

import io.minio.MinioClient;
import offeria.file_service.dto.response.FileResponseDTO;
import offeria.file_service.entity.FileMetadata;
import offeria.file_service.mapper.FileMetadataMapper;
import offeria.file_service.repository.FileMetadataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private FileMetadataRepository repository;

    @Mock
    private FileMetadataMapper mapper;

    @InjectMocks
    private FileServiceImpl fileService;

    private UUID fileId;
    private FileMetadata metadata;

    @BeforeEach
    void setUp() {
        fileId = UUID.randomUUID();
        metadata = FileMetadata.builder()
                .id(fileId)
                .fileName("test.txt")
                .originalFileName("test.txt")
                .contentType("text/plain")
                .size(10L)
                .bucketName("test-bucket")
                .objectPath("test.txt")
                .isPublic(false)
                .build();
        ReflectionTestUtils.setField(fileService, "bucketName", "test-bucket");
    }

    @Test
    void getFileMetadata_Success() {
        // Arrange
        when(repository.findById(fileId)).thenReturn(Optional.of(metadata));
        when(mapper.toDto(metadata)).thenReturn(new FileResponseDTO());

        // Act
        FileResponseDTO result = fileService.getFileMetadata(fileId);

        // Assert
        assertNotNull(result);
        verify(repository).findById(fileId);
    }

    @Test
    void uploadFile_Success() throws Exception {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("test content".getBytes()));
        when(file.getSize()).thenReturn(12L);
        when(file.getContentType()).thenReturn("text/plain");

        when(minioClient.bucketExists(any())).thenReturn(true);
        when(repository.save(any(FileMetadata.class))).thenReturn(metadata);
        when(mapper.toDto(any(FileMetadata.class))).thenReturn(new FileResponseDTO());

        // Act
        FileResponseDTO result = fileService.uploadFile(file, false);

        // Assert
        assertNotNull(result);
        verify(minioClient).putObject(any());
        verify(repository).save(any(FileMetadata.class));
    }
}
