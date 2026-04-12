package com.ael.authservice.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Rol kodu (örn. USER, ADMIN) — token içindeki role claim */
    @Column(nullable = false, unique = true, length = 64)
    private String code;

    @Column(length = 128)
    private String name;

    /**
     * Ürün / mikroservis alanı (örn. {@code RENT}). Genel platform rolleri ({@code USER}, {@code ADMIN}) için
     * {@code null}.
     */
    @Column(name = "service_code", length = 32)
    private String serviceCode;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_authorities",
            joinColumns = @JoinColumn(name = "role_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "authority_id", nullable = false))
    @Builder.Default
    private Set<Authority> authorities = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
