package cz.cvut.ear.DarkstoreApi.controller;

import cz.cvut.ear.DarkstoreApi.dto.ErrorDetails;
import cz.cvut.ear.DarkstoreApi.exception.CourierNotFoundException;
import cz.cvut.ear.DarkstoreApi.exception.OrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({CourierNotFoundException.class, OrderNotFoundException.class})
    public ResponseEntity<?> handleNotFoundException(RuntimeException ex) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
