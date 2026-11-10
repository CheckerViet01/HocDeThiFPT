package com.example.websome.service;

import com.example.websome.dto.ExamCreateUpdateDto;
import com.example.websome.dto.ExamDto;
import com.example.websome.entity.Exam;
import com.example.websome.entity.Subject;
import com.example.websome.entity.User;
import com.example.websome.repository.ExamRepository;
import com.example.websome.repository.SubjectRepository;
import com.example.websome.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    public List<ExamDto> getExams(String examType, Long subjectId, String semester, String search) {
        String cleanType = (examType != null && !examType.equalsIgnoreCase("ALL")) ? examType : null;
        String cleanSemester = (semester != null && !semester.equalsIgnoreCase("ALL")) ? semester : null;
        String cleanSearch = (search != null && !search.isBlank()) ? search.trim() : null;

        return examRepository.findWithFilters(cleanType, subjectId, cleanSemester, cleanSearch)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<String> getSemesters() {
        return examRepository.findDistinctSemesters();
    }

    public ExamDto getExamById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đề thi với ID: " + id));
        exam.setViewsCount(exam.getViewsCount() + 1);
        exam = examRepository.save(exam);
        return toDto(exam);
    }

    @Transactional
    public ExamDto createExam(ExamCreateUpdateDto dto, Long authorId) {
        Subject subject = resolveSubject(dto);
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người đăng ID: " + authorId));

        String title = dto.getTitle();
        if ((title == null || title.isBlank()) && dto.getExamCode() != null) {
            title = dto.getExamCode().trim();
        }
        if (title == null || title.isBlank()) {
            title = "Đề thi " + subject.getCode() + " " + (dto.getSemester() != null ? dto.getSemester() : "");
        }

        String finalImg = dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()
                ? String.join(",", dto.getImageUrls())
                : dto.getImageUrl();

        String finalZip = dto.getZipUrls() != null && !dto.getZipUrls().isEmpty()
                ? String.join(",", dto.getZipUrls())
                : dto.getZipUrl();

        Exam exam = Exam.builder()
                .title(title)
                .subject(subject)
                .examType(dto.getExamType() != null ? dto.getExamType().toUpperCase() : "PE")
                .semester(dto.getSemester() != null ? dto.getSemester().trim() : "")
                .durationMinutes(dto.getDurationMinutes() != null ? dto.getDurationMinutes() : 90)
                .totalMarks(dto.getTotalMarks() != null ? dto.getTotalMarks() : BigDecimal.valueOf(10.0))
                .difficulty(dto.getDifficulty() != null ? dto.getDifficulty().toUpperCase() : "MEDIUM")
                .description(dto.getDescription())
                .content(dto.getContent() != null ? dto.getContent() : "")
                .solution(dto.getSolution())
                .fileUrl(dto.getFileUrl())
                .imageUrl(finalImg)
                .zipUrl(finalZip)
                .author(author)
                .status(dto.getStatus() != null ? dto.getStatus() : "PUBLISHED")
                .viewsCount(0)
                .downloadsCount(0)
                .likesCount(0)
                .build();

        return toDto(examRepository.save(exam));
    }

    @Transactional
    public ExamDto updateExam(Long id, ExamCreateUpdateDto dto) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đề thi ID: " + id));

        if (dto.getSubjectId() != null || (dto.getSubjectCode() != null && !dto.getSubjectCode().isBlank())) {
            Subject subject = resolveSubject(dto);
            exam.setSubject(subject);
        }

        String title = dto.getTitle();
        if ((title == null || title.isBlank()) && dto.getExamCode() != null) {
            title = dto.getExamCode().trim();
        }
        if (title != null && !title.isBlank()) {
            exam.setTitle(title);
        }

        if (dto.getExamType() != null) exam.setExamType(dto.getExamType().toUpperCase());
        if (dto.getSemester() != null) exam.setSemester(dto.getSemester().trim());
        if (dto.getDurationMinutes() != null) exam.setDurationMinutes(dto.getDurationMinutes());
        if (dto.getTotalMarks() != null) exam.setTotalMarks(dto.getTotalMarks());
        if (dto.getDifficulty() != null) exam.setDifficulty(dto.getDifficulty().toUpperCase());
        if (dto.getDescription() != null) exam.setDescription(dto.getDescription());
        if (dto.getContent() != null) exam.setContent(dto.getContent());
        if (dto.getSolution() != null) exam.setSolution(dto.getSolution());
        if (dto.getFileUrl() != null) exam.setFileUrl(dto.getFileUrl());

        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            exam.setImageUrl(String.join(",", dto.getImageUrls()));
        } else if (dto.getImageUrl() != null) {
            exam.setImageUrl(dto.getImageUrl());
        }

        if (dto.getZipUrls() != null && !dto.getZipUrls().isEmpty()) {
            exam.setZipUrl(String.join(",", dto.getZipUrls()));
        } else if (dto.getZipUrl() != null) {
            exam.setZipUrl(dto.getZipUrl());
        }

        if (dto.getStatus() != null) exam.setStatus(dto.getStatus());

        return toDto(examRepository.save(exam));
    }

    private Subject resolveSubject(ExamCreateUpdateDto dto) {
        if (dto.getSubjectId() != null) {
            return subjectRepository.findById(dto.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học ID: " + dto.getSubjectId()));
        }
        if (dto.getSubjectCode() != null && !dto.getSubjectCode().isBlank()) {
            String code = dto.getSubjectCode().trim().toUpperCase();
            return subjectRepository.findByCodeIgnoreCase(code)
                    .orElseGet(() -> {
                        Subject newSub = Subject.builder()
                                .code(code)
                                .name(code)
                                .category("Chuyên ngành")
                                .description("Môn học được thêm từ đề thi")
                                .build();
                        return subjectRepository.save(newSub);
                    });
        }
        throw new RuntimeException("Vui lòng nhập môn học cho đề thi!");
    }

    @Transactional
    public void deleteExam(Long id) {
        if (!examRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy đề thi ID: " + id);
        }
        examRepository.deleteById(id);
    }

    @Transactional
    public void incrementDownload(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đề thi ID: " + id));
        exam.setDownloadsCount(exam.getDownloadsCount() + 1);
        examRepository.save(exam);
    }

    @Transactional
    public int toggleLike(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đề thi ID: " + id));
        exam.setLikesCount(exam.getLikesCount() + 1);
        exam = examRepository.save(exam);
        return exam.getLikesCount();
    }

    public ExamDto toDto(Exam e) {
        List<String> imgList = new ArrayList<>();
        if (e.getImageUrl() != null && !e.getImageUrl().isBlank()) {
            for (String s : e.getImageUrl().split(",")) {
                if (!s.trim().isEmpty()) {
                    imgList.add(s.trim());
                }
            }
        }

        List<String> zList = new ArrayList<>();
        if (e.getZipUrl() != null && !e.getZipUrl().isBlank()) {
            for (String s : e.getZipUrl().split(",")) {
                if (!s.trim().isEmpty()) {
                    zList.add(s.trim());
                }
            }
        }

        return ExamDto.builder()
                .id(e.getId())
                .title(e.getTitle())
                .subjectId(e.getSubject() != null ? e.getSubject().getId() : null)
                .subjectCode(e.getSubject() != null ? e.getSubject().getCode() : "")
                .subjectName(e.getSubject() != null ? e.getSubject().getName() : "")
                .subjectCategory(e.getSubject() != null ? e.getSubject().getCategory() : "")
                .examType(e.getExamType())
                .semester(e.getSemester())
                .durationMinutes(e.getDurationMinutes())
                .totalMarks(e.getTotalMarks())
                .difficulty(e.getDifficulty())
                .description(e.getDescription())
                .content(e.getContent())
                .solution(e.getSolution())
                .fileUrl(e.getFileUrl())
                .imageUrl(e.getImageUrl())
                .imageUrls(imgList)
                .zipUrl(e.getZipUrl())
                .zipUrls(zList)
                .viewsCount(e.getViewsCount())
                .downloadsCount(e.getDownloadsCount())
                .likesCount(e.getLikesCount())
                .authorId(e.getAuthor() != null ? e.getAuthor().getId() : null)
                .authorName(e.getAuthor() != null ? e.getAuthor().getFullName() : "")
                .authorAvatar(e.getAuthor() != null ? e.getAuthor().getAvatarUrl() : "")
                .status(e.getStatus())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
