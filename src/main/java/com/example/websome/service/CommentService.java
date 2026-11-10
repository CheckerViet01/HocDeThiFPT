package com.example.websome.service;

import com.example.websome.dto.CommentDto;
import com.example.websome.entity.Comment;
import com.example.websome.entity.Exam;
import com.example.websome.entity.User;
import com.example.websome.repository.CommentRepository;
import com.example.websome.repository.ExamRepository;
import com.example.websome.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ExamRepository examRepository;
    private final UserRepository userRepository;

    public List<CommentDto> getCommentsByExam(Long examId) {
        return commentRepository.findByExamIdOrderByCreatedAtDesc(examId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto addComment(Long examId, Long userId, String content) {
        return addComment(examId, userId, content, null);
    }

    @Transactional
    public CommentDto addComment(Long examId, Long userId, String content, Integer imageIndex) {
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("Nội dung bình luận không được rỗng.");
        }

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đề thi ID: " + examId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng ID: " + userId));

        Comment comment = Comment.builder()
                .exam(exam)
                .user(user)
                .content(content.trim())
                .imageIndex(imageIndex)
                .build();

        return toDto(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long commentId, Long requestingUserId, String requestingUserRole) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận."));

        if (!"ROLE_ADMIN".equalsIgnoreCase(requestingUserRole) && !comment.getUser().getId().equals(requestingUserId)) {
            throw new RuntimeException("Bạn không có quyền xóa bình luận này.");
        }

        commentRepository.deleteById(commentId);
    }

    public CommentDto toDto(Comment c) {
        return CommentDto.builder()
                .id(c.getId())
                .examId(c.getExam().getId())
                .userId(c.getUser().getId())
                .userName(c.getUser().getFullName())
                .userAvatar(c.getUser().getAvatarUrl())
                .userMssv(c.getUser().getMssv())
                .userRole(c.getUser().getRole())
                .content(c.getContent())
                .imageIndex(c.getImageIndex())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
