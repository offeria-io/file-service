package offeria.file_service.repository;

import offeria.file_service.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for accessing file metadata in the database.
 */
@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, UUID> {
}
