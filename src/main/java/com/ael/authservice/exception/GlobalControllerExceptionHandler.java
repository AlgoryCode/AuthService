package com.ael.authservice.exception;

import com.ael.authservice.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.validation.FieldError;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler extends RuntimeException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("Validation error occurred: {}", ex.getMessage());


        // 2. Alan bazlı hataları topla (opsiyonel - daha detaylı)
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing // Aynı alan için birden fazla hata varsa ilkini al
                ));

        // 3. ProblemDetail oluştur
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Gönderilen veriler doğrulanamadı"
        );

        problemDetail.setTitle("Validation Failed");
        problemDetail.setType(URI.create("https://api.greenproject.com/errors/validation"));
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        // 4. Özel alanlar ekle
        problemDetail.setProperty("fieldErrors", fieldErrors);
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * ResponseStatusException'ı gerçek HTTP koduyla döndürür.
     * Aksi halde {@link #handleGenericException} yakalayıp 500 yapıyordu (örn. yanlış TOTP → 401 yerine 500).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getClass().getSimpleName(),
                System.currentTimeMillis(),
                Collections.singletonList(ex.getMessage()),
                Collections.emptyMap());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        int statusCode = ex.getStatusCode().value();
        HttpStatus resolved = HttpStatus.resolve(statusCode);
        String reason = ex.getReason() != null
                ? ex.getReason()
                : (resolved != null ? resolved.getReasonPhrase() : "Error");
        log.warn("ResponseStatus {} : {}", statusCode, reason);
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getClass().getSimpleName(),
                System.currentTimeMillis(),
                Collections.singletonList(reason),
                Collections.emptyMap());
        return ResponseEntity.status(statusCode).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)  // Diğer beklenmeyen hatalar
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception) {
        log.error("Exception occurred: {}", exception.getMessage(), exception);
        ErrorResponse errorResponse = new ErrorResponse(
                exception.getClass().getSimpleName(),                    // exception field
                System.currentTimeMillis(),           // timestamp field
                Collections.singletonList(exception.getMessage()), // errors field
                Collections.emptyMap()                // fieldErrors field
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
