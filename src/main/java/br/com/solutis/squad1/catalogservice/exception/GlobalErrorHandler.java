package br.com.solutis.squad1.catalogservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalErrorHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalErrorHandler.class);

    private static List<ErrorType> getErrors(List<FieldError> fieldErrors) {
        return fieldErrors
                .stream()
                .map(fieldError -> new ErrorType(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleBadRequestException(BadRequestException ex) {
        LOGGER.error("Bad request", ex);
        return new ExceptionResponse(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                List.of(new ErrorType("body", ex.getMessage())),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleEntityNotFoundException(EntityNotFoundException ex) {
        LOGGER.error("Entity not found", ex);
        return new ExceptionResponse(
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                HttpStatus.NOT_FOUND.value(),
                List.of(new ErrorType("body", ex.getMessage())),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        LOGGER.error("Method argument not valid", ex);
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<ErrorType> errors = getErrors(fieldErrors);

        return new ExceptionResponse(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                errors,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        LOGGER.error("Http message not readable", ex);
        return new ExceptionResponse(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                List.of(new ErrorType("body", ex.getLocalizedMessage())),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleException(Exception ex) {
        LOGGER.error("Internal server error", ex);
        return new ExceptionResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                List.of(new ErrorType("body", ex.getLocalizedMessage())),
                LocalDateTime.now()
        );
    }

    public record ErrorType(String field, String message) {
    }

    public record ExceptionResponse(String message, int status, List<ErrorType> errors, LocalDateTime timestamp) {
    }
}
