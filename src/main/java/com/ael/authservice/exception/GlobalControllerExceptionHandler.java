package com.ael.authservice.exception;


import com.ael.authservice.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice  // ✅ Tüm controller'ları kapsar
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

    @ExceptionHandler(Exception.class)  // ✅ Tüm exception'ları yakalar
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
