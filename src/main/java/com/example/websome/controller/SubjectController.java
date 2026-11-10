package com.example.websome.controller;

import com.example.websome.entity.Subject;
import com.example.websome.service.SubjectService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSubjectById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(subjectService.getSubjectById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createSubject(@RequestBody Subject subject, HttpServletRequest request) {
        String role = (String) request.getAttribute("currentRole");
        if (!"ROLE_ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Chỉ có Admin mới có quyền thêm môn học!"));
        }
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.createSubject(subject));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

