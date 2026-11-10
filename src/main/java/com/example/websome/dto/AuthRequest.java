package com.example.websome.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String fullName;
    private String avatarUrl;
    private String idToken;
    private String mssv;
}

