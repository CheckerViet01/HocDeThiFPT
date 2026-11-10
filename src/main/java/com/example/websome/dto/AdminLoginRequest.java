package com.example.websome.dto;

import lombok.Data;

@Data
public class AdminLoginRequest {
    private String email;
    private String password;
}
