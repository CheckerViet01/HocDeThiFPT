package com.example.websome.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ExamCreateUpdateDto {
    private String title; // Mã đề thi
    private String examCode; // Mã đề thi (alternative alias)
    private Long subjectId;
    private String subjectCode; // Flexible subject input (e.g. CSD201, PRO192)
    private String examType; // PE, FE, QUIZ, PT, MIDTERM
    private String semester; // Spring 2025, Fall 2024...
    private Integer durationMinutes;
    private BigDecimal totalMarks;
    private String difficulty; // EASY, MEDIUM, HARD, NIGHTMARE
    private String description;
    private String content;
    private String solution;
    private String fileUrl;
    private String imageUrl; // Single or comma-separated image URL
    private List<String> imageUrls; // List of all uploaded image URLs
    private String zipUrl; // Single or comma-separated zip URL
    private List<String> zipUrls; // List of all uploaded zip URLs
    private String status; // PUBLISHED, DRAFT
}
