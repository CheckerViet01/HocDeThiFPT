package com.example.websome.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private final Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();

    public FileUploadController() {
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File ảnh không được rỗng!"));
        }

        try {
            Map<String, Object> res = saveSingleFile(file, "exam_img_", ".png");
            return ResponseEntity.ok(res);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Không thể lưu file ảnh: " + ex.getMessage()));
        }
    }

    @PostMapping("/images")
    public ResponseEntity<?> uploadMultipleImages(@RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Danh sách ảnh không được rỗng!"));
        }

        List<Map<String, Object>> results = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                try {
                    results.add(saveSingleFile(file, "exam_img_", ".png"));
                } catch (IOException e) {
                    errors.add(file.getOriginalFilename() + ": " + e.getMessage());
                }
            }
        }

        return ResponseEntity.ok(Map.of(
                "uploaded", results,
                "count", results.size(),
                "errors", errors
        ));
    }

    @PostMapping("/zip")
    public ResponseEntity<?> uploadZip(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File zip không được rỗng!"));
        }

        try {
            Map<String, Object> res = saveSingleFile(file, "exam_given_", ".zip");
            return ResponseEntity.ok(res);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Không thể lưu file zip: " + ex.getMessage()));
        }
    }

    @PostMapping("/zips")
    public ResponseEntity<?> uploadMultipleZips(@RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Danh sách file zip không được rỗng!"));
        }

        List<Map<String, Object>> results = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                try {
                    results.add(saveSingleFile(file, "exam_given_", ".zip"));
                } catch (IOException e) {
                    errors.add(file.getOriginalFilename() + ": " + e.getMessage());
                }
            }
        }

        return ResponseEntity.ok(Map.of(
                "uploaded", results,
                "count", results.size(),
                "errors", errors
        ));
    }

    private Map<String, Object> saveSingleFile(MultipartFile file, String prefix, String defaultExt) throws IOException {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : prefix + defaultExt);
        String extension = "";
        int extIndex = originalFileName.lastIndexOf('.');
        if (extIndex > 0) {
            extension = originalFileName.substring(extIndex);
        } else {
            extension = defaultExt;
        }

        String newFileName = prefix + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
        Path targetLocation = uploadDir.resolve(newFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        String fileUrl = "http://localhost:8081/uploads/" + newFileName;
        return Map.of(
                "fileName", newFileName,
                "originalName", originalFileName,
                "url", fileUrl,
                "size", file.getSize()
        );
    }
}
