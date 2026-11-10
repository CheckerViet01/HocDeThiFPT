package com.example.websome.service;

import com.example.websome.dto.DashboardStatsDto;
import com.example.websome.entity.Exam;
import com.example.websome.repository.ExamRepository;
import com.example.websome.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ExamRepository examRepository;
    private final UserRepository userRepository;

    public DashboardStatsDto getStats() {
        List<Exam> exams = examRepository.findAll();
        long totalUsers = userRepository.count();

        long totalViews = exams.stream().mapToLong(e -> e.getViewsCount() != null ? e.getViewsCount() : 0).sum();
        long totalDownloads = exams.stream().mapToLong(e -> e.getDownloadsCount() != null ? e.getDownloadsCount() : 0).sum();
        long totalLikes = exams.stream().mapToLong(e -> e.getLikesCount() != null ? e.getLikesCount() : 0).sum();

        Map<String, Long> examsByType = new HashMap<>();
        Map<String, Long> examsBySubject = new HashMap<>();

        for (Exam e : exams) {
            String type = e.getExamType();
            examsByType.put(type, examsByType.getOrDefault(type, 0L) + 1);

            String subject = e.getSubject() != null ? e.getSubject().getCode() : "OTHER";
            examsBySubject.put(subject, examsBySubject.getOrDefault(subject, 0L) + 1);
        }

        return DashboardStatsDto.builder()
                .totalExams(exams.size())
                .totalUsers(totalUsers)
                .totalViews(totalViews)
                .totalDownloads(totalDownloads)
                .totalLikes(totalLikes)
                .examsByType(examsByType)
                .examsBySubject(examsBySubject)
                .build();
    }
}

