package com.example.gift_api_remaster.exception;

import com.example.gift_api_remaster.exception.model.ExceptionDto;
import com.example.gift_api_remaster.exception.model.ValidationExceptionDto;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ChildNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleChildNotFoundException(ChildNotFoundException ex) {
        return new ExceptionDto(ex.getMessage());
    }

    @ExceptionHandler(GiftApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleGiftApiException(GiftApiException ex) {
        return new ExceptionDto(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleValidationException(MethodArgumentNotValidException ex) {
        ValidationExceptionDto validationExceptionDto = new ValidationExceptionDto();
        ex.getFieldErrors().forEach(fieldError ->
            validationExceptionDto.addViolation(fieldError.getField(), fieldError.getDefaultMessage())
        );
        return validationExceptionDto;
    }

}