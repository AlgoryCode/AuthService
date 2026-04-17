package com.ael.authservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** {@code registered == true} ise e-posta ile kayıtlı bir kullanıcı vardır. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRegisteredCheckResponse {

    private boolean registered;
}
