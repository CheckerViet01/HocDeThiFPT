package com.example.websome.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {
    private long totalExams;
    private long totalUsers;
    private long totalViews;
    private long totalDownloads;
    private long totalLikes;
    private Map<String, Long> examsByType;
    private Map<String, Long> examsBySubject;
}

