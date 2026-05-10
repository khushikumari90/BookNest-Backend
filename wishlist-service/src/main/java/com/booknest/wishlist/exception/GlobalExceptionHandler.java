package com.booknest.wishlist.exception;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WishlistNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(WishlistNotFoundException ex) {
        return buildError(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        return buildError(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception ex) {
        return buildError("Internal server error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Map<String, String>> buildError(String msg, HttpStatus status) {
        Map<String, String> err = new LinkedHashMap<>();
        err.put("status", String.valueOf(status.value()));
        err.put("error", msg);
        return new ResponseEntity<>(err, status);
    }
}
