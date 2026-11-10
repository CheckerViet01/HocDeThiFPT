package com.example.websome.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String mssv;
    private String fullName;
    private String avatarUrl;
    private String role;
    private String status;
    private String provider;
    private Boolean isMasterAdmin;
    private LocalDateTime createdAt;
}
