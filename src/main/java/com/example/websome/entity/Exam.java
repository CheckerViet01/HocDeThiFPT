package com.example.websome.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "exams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String title; // Mã đề thi hoặc Tiêu đề

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "exam_type", nullable = false, length = 50)
    private String examType; // PE, FE, QUIZ, PT, MIDTERM

    @Column(nullable = false, length = 50)
    private String semester; // Spring 2025, Fall 2024, Summer 2024...

    @Column(name = "duration_minutes", nullable = false)
    @Builder.Default
    private Integer durationMinutes = 90;

    @Column(name = "total_marks", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal totalMarks = BigDecimal.valueOf(10.0);

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String difficulty = "MEDIUM"; // EASY, MEDIUM, HARD, NIGHTMARE

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String solution;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(name = "image_url", columnDefinition = "NVARCHAR(MAX)")
    private String imageUrl; // Uploaded exam sheet image

    @Column(name = "zip_url", columnDefinition = "NVARCHAR(MAX)")
    private String zipUrl; // Uploaded .zip given file

    @Column(name = "views_count", nullable = false)
    @Builder.Default
    private Integer viewsCount = 0;

    @Column(name = "downloads_count", nullable = false)
    @Builder.Default
    private Integer downloadsCount = 0;

    @Column(name = "likes_count", nullable = false)
    @Builder.Default
    private Integer likesCount = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PUBLISHED"; // PUBLISHED, DRAFT

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
