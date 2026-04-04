package offeria.file_service.service.impl;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import offeria.file_service.dto.response.FileResponseDTO;
import offeria.file_service.entity.FileMetadata;
import offeria.file_service.exception.FileNotFoundException;
import offeria.file_service.exception.FileServiceException;
import offeria.file_service.mapper.FileMetadataMapper;
import offeria.file_service.repository.FileMetadataRepository;
import offeria.file_service.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of FileService handling storage and metadata.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    private final MinioClient minioClient;
    private final FileMetadataRepository repository;
    private final FileMetadataMapper mapper;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Override
    @Transactional
    public FileResponseDTO uploadFile(MultipartFile file, boolean isPublic) {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String objectName = UUID.randomUUID() + fileExtension;

        try {
            // Ensure bucket exists
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            // Upload to MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // Save Metadata
            FileMetadata metadata = FileMetadata.builder()
                    .fileName(objectName)
                    .originalFileName(originalFilename)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .bucketName(bucketName)
                    .objectPath(objectName)
                    .isPublic(isPublic)
                    .build();

            FileMetadata savedMetadata = repository.save(metadata);
            FileResponseDTO response = mapper.toDto(savedMetadata);
            response.setDownloadUrl(generateDownloadUrl(savedMetadata.getId()));
            
            log.info("Successfully uploaded file: {} with ID: {}", originalFilename, savedMetadata.getId());
            return response;

        } catch (Exception e) {
            log.error("Error occurred while uploading file: ", e);
            throw new FileServiceException("Failed to upload file to storage", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Resource downloadFile(UUID fileId) {
        FileMetadata metadata = repository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found with ID: " + fileId));

        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(metadata.getBucketName())
                            .object(metadata.getObjectPath())
                            .build()
            );
            return new InputStreamResource(stream);
        } catch (Exception e) {
            log.error("Error occurred while downloading file: ", e);
            throw new FileServiceException("Failed to download file from storage", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public FileResponseDTO getFileMetadata(UUID fileId) {
        FileMetadata metadata = repository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found with ID: " + fileId));
        
        FileResponseDTO dto = mapper.toDto(metadata);
        dto.setDownloadUrl(generateDownloadUrl(fileId));
        return dto;
    }

    @Override
    public String generateDownloadUrl(UUID fileId) {
        FileMetadata metadata = repository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found with ID: " + fileId));

        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(metadata.getBucketName())
                            .object(metadata.getObjectPath())
                            .expiry(1, TimeUnit.HOURS)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error generating pre-signed URL: ", e);
            return null;
        }
    }
}
