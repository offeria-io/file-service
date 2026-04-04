package offeria.file_service.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a file is not found.
 */
public class FileNotFoundException extends FileServiceException {
    public FileNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
