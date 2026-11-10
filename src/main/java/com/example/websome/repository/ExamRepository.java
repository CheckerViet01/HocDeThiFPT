package com.example.websome.repository;

import com.example.websome.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    @Query("SELECT e FROM Exam e WHERE e.status = 'PUBLISHED' " +
           "AND (:examType IS NULL OR e.examType = :examType) " +
           "AND (:subjectId IS NULL OR e.subject.id = :subjectId) " +
           "AND (:semester IS NULL OR e.semester = :semester) " +
           "AND (:search IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(e.subject.code) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(e.subject.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY e.createdAt DESC")
    List<Exam> findWithFilters(
            @Param("examType") String examType,
            @Param("subjectId") Long subjectId,
            @Param("semester") String semester,
            @Param("search") String search
    );

    @Query("SELECT DISTINCT e.semester FROM Exam e WHERE e.semester IS NOT NULL AND e.semester != '' ORDER BY e.semester DESC")
    List<String> findDistinctSemesters();

    List<Exam> findByExamType(String examType);

    List<Exam> findBySubjectId(Long subjectId);

    long countByExamType(String examType);
}
