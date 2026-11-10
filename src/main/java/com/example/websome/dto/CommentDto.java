package com.example.websome.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private Long examId;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String userMssv;
    private String userRole;
    private String content;
    private Integer imageIndex; // Chỉ số ảnh (0, 1, 2...) hoặc null nếu bình luận chung
    private LocalDateTime createdAt;
}
