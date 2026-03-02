package com.ael.authservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String exception;           // "GenericException"
    private Long timestamp;            // 1757374488412
    private List<String> errors;       // ["Beklenmeyen bir hata oluştu!"]
    private Map<String, List<String>> fieldErrors; // {}
}