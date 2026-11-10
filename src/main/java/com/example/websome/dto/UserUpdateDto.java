package com.example.websome.dto;

import lombok.Data;

@Data
public class UserUpdateDto {
    private String fullName;
    private String mssv;
    private String role;   // ROLE_ADMIN or ROLE_USER
    private String status; // ACTIVE or LOCKED
}

