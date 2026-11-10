package com.example.websome.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String identifier; // Email or MSSV
    private String password;
}

