package com.ael.authservice.config.properties;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.rbac")
public class RbacAppProperties {

    private boolean rentQrSeedEnabled = true;

    /**
     * Kayıt sırasında {@code roleCode} ile atanabilecek rol kodları. Boş liste = yalnızca USER (roleCode
     * yok sayılır veya reddedilir).
     */
    private List<String> registrationAllowedRoleCodes = new ArrayList<>(
            List.of("RENT_USER", "RENT_MANAGER", "RENT_ADMIN", "QR_USER", "QR_MANAGER", "QR_ADMIN"));
}
