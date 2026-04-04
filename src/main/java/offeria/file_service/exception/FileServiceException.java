package offeria.file_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base class for all custom exceptions in this service.
 */
@Getter
public class FileServiceException extends RuntimeException {
    private final HttpStatus status;

    public FileServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
