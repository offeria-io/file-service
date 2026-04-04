package offeria.file_service.service;

import offeria.file_service.dto.response.FileResponseDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Service interface for file operations.
 */
public interface FileService {

    /**
     * Uploads a file and saves its metadata.
     * @param file the file to upload
     * @param isPublic whether the file should be publicly accessible
     * @return the saved metadata DTO
     */
    FileResponseDTO uploadFile(MultipartFile file, boolean isPublic);

    /**
     * Downloads a file from storage.
     * @param fileId the UUID of the file
     * @return the file content as a Resource
     */
    Resource downloadFile(UUID fileId);

    /**
     * Retrieves file metadata by ID.
     * @param fileId the UUID of the file
     * @return the metadata DTO
     */
    FileResponseDTO getFileMetadata(UUID fileId);

    /**
     * Generates a pre-signed URL for downloading the file.
     * @param fileId the UUID of the file
     * @return the pre-signed URL
     */
    String generateDownloadUrl(UUID fileId);
}
