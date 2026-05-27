package com.sayuri.dqchecker.repository;

import com.sayuri.dqchecker.entity.ValidationReport;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ValidationReportRepository extends JpaRepository<ValidationReport, Long> {

    @EntityGraph(attributePaths = {"rules", "uploadedFile", "uploadedFile.user"})
    Optional<ValidationReport> findWithRulesById(Long id);

    @EntityGraph(attributePaths = {"rules", "uploadedFile"})
    List<ValidationReport> findByUploadedFileUserEmailOrderByCreatedAtDesc(String email);

    @EntityGraph(attributePaths = {"rules", "uploadedFile"})
    List<ValidationReport> findAllByOrderByCreatedAtDesc();
}
