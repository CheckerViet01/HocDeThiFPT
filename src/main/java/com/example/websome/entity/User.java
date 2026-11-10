package com.example.websome.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 255)
    private String password; // Password for sub-admins or local login

    @Column(name = "is_master_admin", nullable = false)
    @Builder.Default
    private Boolean isMasterAdmin = false; // Only admin@fpt.edu.vn is master admin

    @Column(length = 50)
    private String mssv; // Mã số sinh viên FPT (e.g., SE170123)

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String role = "ROLE_USER"; // ROLE_ADMIN, ROLE_USER

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, LOCKED

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String provider = "GOOGLE"; // GOOGLE, LOCAL

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isMasterAdmin == null) isMasterAdmin = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
