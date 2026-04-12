package com.ael.authservice.dto.request;

/**
 * Tüm kayıt isteklerinin ortak sözleşmesi. Yeni sağlayıcılar (ör. GitHub) için {@code permits} listesine
 * eklenir.
 */
public sealed interface RegisterRequest permits BasicRegisterRequest, GoogleRegisterRequest {

    /** Atanacak rol; istemci göndermezse sağlayıcılar {@link RegistrationRole#USER} varsayar. */
    RegistrationRole registrationRole();
}
