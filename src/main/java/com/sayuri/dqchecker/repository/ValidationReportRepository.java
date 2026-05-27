package com.sayuri.dqchecker.repository;

import com.sayuri.dqchecker.entity.ValidationReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ValidationReportRepository extends JpaRepository<ValidationReport, Long> {

    @EntityGraph(attributePaths = {"results", "rules", "uploadedFile", "uploadedFile.user"})
    Optional<ValidationReport> findWithRulesById(Long id);

    @EntityGraph(attributePaths = {"rules", "uploadedFile"})
    List<ValidationReport> findByUploadedFileUserEmailOrderByCreatedAtDesc(String email);

    @EntityGraph(attributePaths = {"rules", "uploadedFile"})
    List<ValidationReport> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"uploadedFile", "uploadedFile.user"})
    @Query("""
            select r from ValidationReport r
            join r.uploadedFile f
            join f.user u
            where (:admin = true or u.email = :email)
              and (:passed is null or r.passed = :passed)
              and (:filename is null or lower(f.originalFilename) like lower(concat('%', :filename, '%')))
            order by r.createdAt desc
            """)
    Page<ValidationReport> searchReports(@Param("email") String email,
                                         @Param("admin") boolean admin,
                                         @Param("passed") Boolean passed,
                                         @Param("filename") String filename,
                                         Pageable pageable);

    long countByUploadedFileUserEmail(String email);

    long countByUploadedFileUserEmailAndPassedFalse(String email);

    long countByPassedFalse();
}
