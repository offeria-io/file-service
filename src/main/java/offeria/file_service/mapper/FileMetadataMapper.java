package offeria.file_service.mapper;

import offeria.file_service.dto.response.FileResponseDTO;
import offeria.file_service.entity.FileMetadata;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting between FileMetadata entity and DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileMetadataMapper {

    /**
     * Converts FileMetadata entity to FileResponseDTO.
     * Note: downloadUrl is handled separately in the service layer.
     * @param entity the metadata entity
     * @return the response DTO
     */
    @Mapping(target = "downloadUrl", ignore = true)
    FileResponseDTO toDto(FileMetadata entity);
}
