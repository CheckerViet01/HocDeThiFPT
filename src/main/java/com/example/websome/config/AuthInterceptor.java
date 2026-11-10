package com.example.websome.config;

import com.example.websome.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtService.validateToken(token)) {
                request.setAttribute("currentEmail", jwtService.getEmailFromToken(token));
                request.setAttribute("currentRole", jwtService.getRoleFromToken(token));
                request.setAttribute("currentUserId", jwtService.getUserIdFromToken(token));
            }
        }
        return true;
    }
}

