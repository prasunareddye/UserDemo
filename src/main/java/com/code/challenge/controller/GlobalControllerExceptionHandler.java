package com.code.challenge.controller;

import com.code.challenge.exception.EntityNotFoundException;
import com.code.challenge.exception.InvalidProfileDataException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
        final MethodArgumentNotValidException ex
    ) {
        return ex.getBindingResult().getAllErrors().stream()
            .collect(Collectors.toMap(
                error -> ((FieldError) error).getField(),
                error -> Optional.ofNullable(error.getDefaultMessage()).orElse("")));

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidProfileDataException.class)
    public Map<String, String> handleBadProfiledata(
        final InvalidProfileDataException ex
    ) {

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Data Missing");
        errorResponse.put("message", ex.getMessage());
        return errorResponse;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public Map<String, String> handleMissingData(
        final EntityNotFoundException ex
    ) {

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Data not found");
        errorResponse.put("message", ex.getMessage());
        return errorResponse;
    }


}
