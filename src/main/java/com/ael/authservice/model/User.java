package com.ael.authservice.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    private String firstName;
    private String lastName;
    @NotNull(message = "Email boş geçilemez")
    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email adresi giriniz")
    private String email;
    @NotNull(message = "Telefon boş geçilemez")
    @NotBlank(message = "Telefon boş olamaz")
    @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "Geçerli bir telefon numarası giriniz")
    @Size(min = 10, max = 15, message = "Telefon numarası 10-15 karakter arasında olmalıdır")
    private String phoneNumber;
    private String address;
    private String city;
    @NotNull(message = "Kullanıcı adı zorunludur")
    @Pattern(
            regexp = "^[a-zA-Z][a-zA-Z0-9_]{3,14}$",
            message = "Kullanıcı adı harf ile başlamalı, 4-15 karakter arasında olmalı ve sadece harf, rakam, _ içermelidir"
    )
    private String username;
    @NotNull(message = "Şifre boş geçilemez")
    @NotBlank(message = "Şifre boş olamaz")
    @Size(min = 6, max = 20, message = "Şifre 6-20 karakter arasında olmalıdır")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Şifre en az 1 küçük harf, 1 büyük harf ve 1 rakam içermelidir")
    private String password;
    private String roles;
}