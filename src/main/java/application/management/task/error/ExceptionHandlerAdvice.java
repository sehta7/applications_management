package application.management.task.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(WrongStateException.class)
    public ResponseEntity<Object> handleWrongStateException(WrongStateException exception){
        ApiError apiError = new ApiError(LocalDateTime.now(), HttpStatus.NOT_ACCEPTABLE, exception.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(ApplicationNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(ApplicationNotFoundException exception){
        ApiError apiError = new ApiError(LocalDateTime.now(), HttpStatus.NOT_FOUND, exception.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
