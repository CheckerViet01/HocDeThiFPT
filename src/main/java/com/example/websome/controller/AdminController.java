package com.example.websome.controller;

import com.example.websome.dto.CreateSubAdminRequest;
import com.example.websome.dto.DashboardStatsDto;
import com.example.websome.dto.UserDto;
import com.example.websome.dto.UserUpdateDto;
import com.example.websome.service.DashboardService;
import com.example.websome.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final DashboardService dashboardService;

    private boolean isAdmin(HttpServletRequest request) {
        String role = (String) request.getAttribute("currentRole");
        return "ROLE_ADMIN".equalsIgnoreCase(role);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Truy cập bị từ chối. Chỉ dành cho Quản trị viên (ADMIN)!"));
        }
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        List<UserDto> users = userService.getAllUsers(currentUserId);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/create-sub-admin")
    public ResponseEntity<?> createSubAdmin(@RequestBody CreateSubAdminRequest body, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Truy cập bị từ chối!"));
        }
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Chưa xác thực danh tính."));
        }
        try {
            UserDto created = userService.createSubAdmin(body, currentUserId);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateDto dto,
            HttpServletRequest request
    ) {
        if (!isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Chỉ có Admin mới có quyền sửa thông tin người dùng!"));
        }
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        try {
            UserDto updated = userService.updateUserByAdmin(id, dto, currentUserId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Chỉ có Admin mới có quyền xóa người dùng!"));
        }
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        try {
            userService.deleteUser(id, currentUserId);
            return ResponseEntity.ok(Map.of("message", "Đã xóa người dùng thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Chỉ có Admin mới có quyền xem thống kê!"));
        }
        DashboardStatsDto stats = dashboardService.getStats();
        return ResponseEntity.ok(stats);
    }
}
