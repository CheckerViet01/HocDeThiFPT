package com.example.websome.service;

import com.example.websome.entity.Subject;
import com.example.websome.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học ID: " + id));
    }

    public Subject createSubject(Subject subject) {
        return subjectRepository.save(subject);
    }
}

