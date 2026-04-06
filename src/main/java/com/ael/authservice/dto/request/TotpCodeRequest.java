package com.ael.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TotpCodeRequest {

    @NotBlank
    @Pattern(regexp = "\\d{6}", message = "Code must be 6 digits")
    private String code;
}
