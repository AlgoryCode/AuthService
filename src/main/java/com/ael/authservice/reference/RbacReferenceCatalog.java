package com.ael.authservice.reference;

/**
 * RBAC isimlendirme sözlüğü — <strong>yalnızca dokümantasyon / hatırlatma</strong> amaçlıdır.
 *
 * <p>Uygulama kodunda (servis, controller, güvenlik) bu sabitler veya enumlar
 * <strong>kullanılmamalıdır</strong>; yetki ve rol kayıtları veritabanında tutulur. Varsayılan olarak aynı
 * küme {@link com.ael.authservice.config.ServiceRbacBootstrap} ile uygulama açılışında idempotent eklenir;
 * alternatif: {@code src/main/resources/db/seed-rbac-rent-qr.sql} (manuel veya init script).
 *
 * <h2>Desen</h2>
 *
 * <ul>
 *   <li>Servis kodu: {@code RENT}, {@code QR} — {@code roles.service_code}
 *   <li>Rol: {@code {SERVİS}_{SEVİYE}} — seviye {@code ADMIN}, {@code MANAGER}, {@code USER}
 *   <li>Yetki: {@code {SERVİS}_{SEVİYE}_{EYLEM}} — eylem {@code READ}, {@code UPDATE}, {@code WRITE}, {@code DELETE}
 * </ul>
 */
public final class RbacReferenceCatalog {

    private RbacReferenceCatalog() {}

    /** {@code roles.service_code} — Kiralama ürünü. */
    public static final String SERVICE_RENT = "RENT";

    /** {@code roles.service_code} — QR ürünü. */
    public static final String SERVICE_QR = "QR";

    /** Rent tarafı rol kodları ({@code roles.code}). */
    public enum RentRole {
        RENT_ADMIN,
        RENT_MANAGER,
        RENT_USER
    }

    /** Rent — yalnızca {@link RentRole#RENT_ADMIN} rolüne bağlanacak yetki kodları. */
    public enum RentAdminAuthority {
        RENT_ADMIN_READ,
        RENT_ADMIN_UPDATE,
        RENT_ADMIN_WRITE,
        RENT_ADMIN_DELETE
    }

    /** Rent — yalnızca {@link RentRole#RENT_MANAGER} rolüne bağlanacak yetki kodları. */
    public enum RentManagerAuthority {
        RENT_MANAGER_READ,
        RENT_MANAGER_UPDATE,
        RENT_MANAGER_WRITE,
        RENT_MANAGER_DELETE
    }

    /** Rent — yalnızca {@link RentRole#RENT_USER} rolüne bağlanacak yetki kodları. */
    public enum RentUserAuthority {
        RENT_USER_READ,
        RENT_USER_UPDATE,
        RENT_USER_WRITE,
        RENT_USER_DELETE
    }

    /** QR tarafı rol kodları ({@code roles.code}). */
    public enum QrRole {
        QR_ADMIN,
        QR_MANAGER,
        QR_USER
    }

    /** QR — yalnızca {@link QrRole#QR_ADMIN} rolüne bağlanacak yetki kodları. */
    public enum QrAdminAuthority {
        QR_ADMIN_READ,
        QR_ADMIN_UPDATE,
        QR_ADMIN_WRITE,
        QR_ADMIN_DELETE
    }

    /** QR — yalnızca {@link QrRole#QR_MANAGER} rolüne bağlanacak yetki kodları. */
    public enum QrManagerAuthority {
        QR_MANAGER_READ,
        QR_MANAGER_UPDATE,
        QR_MANAGER_WRITE,
        QR_MANAGER_DELETE
    }

    /** QR — yalnızca {@link QrRole#QR_USER} rolüne bağlanacak yetki kodları. */
    public enum QrUserAuthority {
        QR_USER_READ,
        QR_USER_UPDATE,
        QR_USER_WRITE,
        QR_USER_DELETE
    }
}
