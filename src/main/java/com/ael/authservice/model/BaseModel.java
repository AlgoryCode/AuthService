package com.ael.authservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class BaseModel {

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column
    private Integer clientId;

    @Column
    private String ipAddress;

    @Column
    private Integer userId;

    @Column(name="is_Deleted")
    private boolean deleted;

    @Column
    private String deviceInfo;

    @Column
    private String userAgent;

    @Column(name = "is_revoked")
    private boolean revoked = false;

    private String appName;


}
