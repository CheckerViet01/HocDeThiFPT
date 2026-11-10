package com.example.websome.controller;

import com.example.websome.dto.*;
import com.example.websome.service.CommentService;
import com.example.websome.service.ExamService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<ExamDto>> getExams(
            @RequestParam(required = false) String examType,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(examService.getExams(examType, subjectId, semester, search));
    }

    @GetMapping("/semesters")
    public ResponseEntity<List<String>> getSemesters() {
        return ResponseEntity.ok(examService.getSemesters());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExamById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(examService.getExamById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createExam(@RequestBody ExamCreateUpdateDto dto, HttpServletRequest request) {
        String role = (String) request.getAttribute("currentRole");
        Long userId = (Long) request.getAttribute("currentUserId");

        if (!"ROLE_ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Chỉ có Admin mới có quyền đăng đề thi mới!"));
        }

        try {
            ExamDto created = examService.createExam(dto, userId != null ? userId : 1L);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExam(@PathVariable Long id, @RequestBody ExamCreateUpdateDto dto, HttpServletRequest request) {
        String role = (String) request.getAttribute("currentRole");
        if (!"ROLE_ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Chỉ có Admin mới có quyền cập nhật đề thi!"));
        }

        try {
            ExamDto updated = examService.updateExam(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExam(@PathVariable Long id, HttpServletRequest request) {
        String role = (String) request.getAttribute("currentRole");
        if (!"ROLE_ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Chỉ có Admin mới có quyền xóa đề thi!"));
        }

        try {
            examService.deleteExam(id);
            return ResponseEntity.ok(Map.of("message", "Đã xóa đề thi thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/download")
    public ResponseEntity<?> downloadExam(@PathVariable Long id) {
        try {
            examService.incrementDownload(id);
            return ResponseEntity.ok(Map.of("message", "Tải xuống thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeExam(@PathVariable Long id) {
        try {
            int likes = examService.toggleLike(id);
            return ResponseEntity.ok(Map.of("likes", likes));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentsByExam(id));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("currentUserId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Vui lòng đăng nhập để bình luận."));
        }

        Object contentObj = body.get("content");
        if (contentObj == null || contentObj.toString().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Nội dung bình luận không được rỗng."));
        }
        String content = contentObj.toString().trim();

        Integer imageIndex = null;
        if (body.get("imageIndex") != null) {
            try {
                imageIndex = Integer.valueOf(body.get("imageIndex").toString());
            } catch (Exception ignored) {}
        }

        try {
            CommentDto created = commentService.addComment(id, userId, content, imageIndex);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        String role = (String) request.getAttribute("currentRole");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Chưa xác thực."));
        }

        try {
            commentService.deleteComment(commentId, userId, role != null ? role : "ROLE_USER");
            return ResponseEntity.ok(Map.of("message", "Đã xóa bình luận."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
