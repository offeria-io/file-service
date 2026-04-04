package offeria.file_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for file metadata information returned to the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponseDTO {
    private UUID id;
    private String fileName;
    private String originalFileName;
    private String contentType;
    private Long size;
    private String downloadUrl;
    private LocalDateTime createdAt;
}
