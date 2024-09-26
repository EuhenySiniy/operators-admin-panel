package yevhen.synii.admin_panel.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import yevhen.synii.admin_panel.dto.ErrorResponse;
import yevhen.synii.admin_panel.exception.*;

import java.time.LocalDateTime;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {
    @ExceptionHandler({
            EmailIsAlreadyTaken.class,
            WrongPassword.class,
            UserIsNotFound.class,
            IllegalArgumentException.class,
            BadRequestException.class
    })
    public ResponseEntity<?> handleModelIncorrectRequestException(Exception e) {
        return getErrorResponseEntity(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            UserHasBeenDeactivated.class
    })
    public ResponseEntity<?> handleModelForbidden(Exception e) {
        return getErrorResponseEntity(e, HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<ErrorResponse> getErrorResponseEntity(Exception e, HttpStatus status) {
        logger.error(e.getMessage(), e);
        return new ResponseEntity<>(
                new ErrorResponse(
                        e.getClass().getSimpleName(),
                        LocalDateTime.now(),
                        status.value(),
                        e.getMessage()
                ),
                status
        );
    }
}
