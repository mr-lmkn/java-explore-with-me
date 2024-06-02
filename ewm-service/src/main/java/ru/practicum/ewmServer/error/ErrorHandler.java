package ru.practicum.ewmServer.error;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.practicum.ewmServer.error.dto.ApiError;
import ru.practicum.ewmServer.error.exceptions.BadRequestException;
import ru.practicum.ewmServer.error.exceptions.ConflictException;
import ru.practicum.ewmServer.error.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
@RestControllerAdvice(annotations = RestController.class)
public class ErrorHandler extends ResponseEntityExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<Object> notFoundException(final NotFoundException e) {
        List<String> errors = new ArrayList<String>();
        for (StackTraceElement violation : e.getStackTrace()) {
            errors.add(
                    violation.getClassName() + " " +
                            violation.getModuleName() + ": " +
                            violation.getMethodName() + " row " +
                            violation.getLineNumber()
            );
        }
        log.info("Error: {}, {}", HttpStatus.NOT_FOUND, e.getMessage(), e);
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND,
                e.getMessage(),
                "The required object was not found",
                errors);
        return new ResponseEntity<Object>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<Object> handleBadRequestException(final BadRequestException e) {
        List<String> errors = new ArrayList<String>();
        log.info("Error: {}, {}", HttpStatus.BAD_REQUEST, e.getMessage(), e);
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,
                e.getMessage(),
                "The required object was not found",
                errors);
        return new ResponseEntity<Object>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({ConflictException.class})
    public ResponseEntity<Object> handleConflictException(final ConflictException e) {
        List<String> errors = new ArrayList<String>();
        for (StackTraceElement violation : e.getStackTrace()) {
            errors.add(
                    violation.getClassName() + " " +
                            violation.getModuleName() + ": " +
                            violation.getMethodName() + " row " +
                            violation.getLineNumber()
            );
        }
        log.info("Error: {}, {}", HttpStatus.CONFLICT, e.getMessage(), e);
        ApiError apiError = new ApiError(HttpStatus.CONFLICT,
                e.getMessage(),
                "Integrity constraint has been violated.",
                errors);
        return new ResponseEntity<Object>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({PSQLException.class})
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        List<String> errors = new ArrayList<String>();
        for (StackTraceElement violation : ex.getStackTrace()) {
            errors.add(
                    violation.getClassName() + " " +
                            violation.getModuleName() + ": " +
                            violation.getMethodName() + " row " +
                            violation.getLineNumber()
            );
        }
        log.info("Error: {}, {}", HttpStatus.CONFLICT, ex.getMessage(), ex);
        ApiError apiError = new ApiError(HttpStatus.CONFLICT,
                ex.getMessage(),
                "Integrity constraint has been violated. ?",
                errors);
        return new ResponseEntity<Object>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        List<String> errors = new ArrayList<String>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), "Not valid data", errors);
        return handleExceptionInternal(
                ex, apiError, headers, apiError.getStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";

        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error, "parameter is missing");
        return new ResponseEntity<Object>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        String error =
                ex.getName() + " should be of type " + ex.getRequiredType().getName();

        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error, "Wrong metod type");
        return new ResponseEntity<Object>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

}
