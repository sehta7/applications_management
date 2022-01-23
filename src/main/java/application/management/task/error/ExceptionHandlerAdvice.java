package application.management.task.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(WrongStateException.class)
    public ResponseEntity<Object> handleWrongStateException(WrongStateException exception){
        ApiError apiError = new ApiError(LocalDateTime.now(), HttpStatus.NOT_ACCEPTABLE, exception.getMessage());
        log.error(apiError.toString());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(ApplicationNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(ApplicationNotFoundException exception){
        ApiError apiError = new ApiError(LocalDateTime.now(), HttpStatus.NOT_FOUND, exception.getMessage());
        log.error(apiError.toString());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
