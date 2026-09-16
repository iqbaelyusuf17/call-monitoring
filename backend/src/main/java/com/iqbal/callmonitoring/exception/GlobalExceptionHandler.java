package com.iqbal.callmonitoring.exception;

import com.iqbal.callmonitoring.dto.response.WebResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<WebResponse<Object>> handleBadRequestException(BadRequestException ex) {
        log.warn("Bad Request Exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(WebResponse.error(400, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<WebResponse<Object>> handleGeneralException(Exception ex) {
        log.error("Internal Server Error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(WebResponse.error(500, "Internal Server Error: " + ex.getMessage()));
    }
}
