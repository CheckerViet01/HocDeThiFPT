package com.example.websome.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamDto {
    private Long id;
    private String title;
    private Long subjectId;
    private String subjectCode;
    private String subjectName;
    private String subjectCategory;
    private String examType; // PE, FE, QUIZ, PT, MIDTERM
    private String semester;
    private Integer durationMinutes;
    private BigDecimal totalMarks;
    private String difficulty;
    private String description;
    private String content;
    private String solution;
    private String fileUrl;
    private String imageUrl; // Single or comma-separated URL
    private List<String> imageUrls; // List of all uploaded image URLs
    private String zipUrl; // Single or comma-separated URL
    private List<String> zipUrls; // List of all uploaded zip URLs
    private Integer viewsCount;
    private Integer downloadsCount;
    private Integer likesCount;
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
