package com.ael.authservice.dto.request;



import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RegisterRequest {
    @NotNull(message = "Kullanıcı adı boş geçilemez")
    @NotBlank(message = "Kullanıcı adı boş geçilemez")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Kullanıcı adı sadece harf, rakam ve _ içerebilir")
    private String username;

    @NotNull(message = "Şifre boş geçilemez")
    @NotBlank(message = "Şifre boş olamaz")
    @Size(min = 6, max = 20, message = "Şifre 6-20 karakter arasında olmalıdır")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Şifre en az 1 küçük harf, 1 büyük harf ve 1 rakam içermelidir")
    private String password;


    @NotNull(message = "Email boş geçilemez")
    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email adresi giriniz")
    private String email;


    @NotNull(message = "Telefon boş geçilemez")
    @NotBlank(message = "Telefon boş olamaz")
    @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "Geçerli bir telefon numarası giriniz")
    @Size(min = 10, max = 15, message = "Telefon numarası 10-15 karakter arasında olmalıdır")
    private String phoneNumber;

}
