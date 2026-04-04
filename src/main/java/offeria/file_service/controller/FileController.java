package offeria.file_service.controller;

import lombok.RequiredArgsConstructor;
import offeria.file_service.dto.response.FileResponseDTO;
import offeria.file_service.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * REST Controller for file management.
 */
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * Uploads a file.
     * @param file the multipart file
     * @param isPublic optional flag to make file public
     * @return the metadata of the uploaded file
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponseDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isPublic", defaultValue = "false") boolean isPublic) {
        return new ResponseEntity<>(fileService.uploadFile(file, isPublic), HttpStatus.CREATED);
    }

    /**
     * Downloads a file by ID.
     * @param fileId the UUID of the file
     * @return the file resource with appropriate headers
     */
    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable UUID fileId) {
        Resource resource = fileService.downloadFile(fileId);
        FileResponseDTO metadata = fileService.getFileMetadata(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getOriginalFileName() + "\"")
                .body(resource);
    }

    /**
     * Gets file metadata by ID.
     * @param fileId the UUID of the file
     * @return the file metadata
     */
    @GetMapping("/{fileId}/metadata")
    public ResponseEntity<FileResponseDTO> getMetadata(@PathVariable UUID fileId) {
        return ResponseEntity.ok(fileService.getFileMetadata(fileId));
    }

    /**
     * Generates a pre-signed download URL for a file.
     * @param fileId the UUID of the file
     * @return the pre-signed URL
     */
    @GetMapping("/{fileId}/url")
    public ResponseEntity<String> getDownloadUrl(@PathVariable UUID fileId) {
        return ResponseEntity.ok(fileService.generateDownloadUrl(fileId));
    }
}
