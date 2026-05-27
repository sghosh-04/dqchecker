package com.sayuri.dqchecker.service;

import com.sayuri.dqchecker.dto.ReportResponse;
import com.sayuri.dqchecker.dto.ReportSummary;
import com.sayuri.dqchecker.dto.RuleResultResponse;
import com.sayuri.dqchecker.entity.ValidationResult;
import com.sayuri.dqchecker.entity.ValidationReport;
import com.sayuri.dqchecker.exception.ReportNotFoundException;
import com.sayuri.dqchecker.repository.ValidationReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final ValidationReportRepository validationReportRepository;

    public ReportService(ValidationReportRepository validationReportRepository) {
        this.validationReportRepository = validationReportRepository;
    }

    public List<ReportResponse> getReports(String email, boolean admin) {
        List<ValidationReport> reports = admin
                ? validationReportRepository.findAllByOrderByCreatedAtDesc()
                : validationReportRepository.findByUploadedFileUserEmailOrderByCreatedAtDesc(email);
        return reports.stream().map(this::toResponse).toList();
    }

    public Page<ReportSummary> getReportHistory(String email, boolean admin, Boolean passed, String filename,
                                                Pageable pageable) {
        String normalizedFilename = filename == null || filename.isBlank() ? null : filename.trim();
        return validationReportRepository.searchReports(email, admin, passed, normalizedFilename, pageable)
                .map(this::toSummary);
    }

    public ReportResponse getReport(Long id, String email, boolean admin) {
        ValidationReport report = validationReportRepository.findWithRulesById(id)
                .orElseThrow(() -> new ReportNotFoundException("Report not found"));
        if (!admin && !report.getUploadedFile().getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("Report does not belong to current user");
        }
        return toResponse(report);
    }

    @Transactional
    public void deleteReport(Long id, String email, boolean admin) {
        ValidationReport report = validationReportRepository.findWithRulesById(id)
                .orElseThrow(() -> new ReportNotFoundException("Report not found"));
        if (!admin && !report.getUploadedFile().getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("Report does not belong to current user");
        }
        validationReportRepository.delete(report);
        log.info("Deleted validation report id={} user={}", id, email);
    }

    private ReportResponse toResponse(ValidationReport report) {
        List<RuleResultResponse> results = report.getResults().isEmpty()
                ? report.getRules().stream()
                .sorted(Comparator.comparing(rule -> rule.getId() == null ? 0L : rule.getId()))
                .map(rule -> new RuleResultResponse(
                        rule.getId(),
                        rule.getRuleName(),
                        rule.isPassed(),
                        rule.getErrorCount(),
                        rule.getErrorMessages()))
                .toList()
                : report.getResults().stream()
                .sorted(Comparator.comparing(result -> result.getId() == null ? 0L : result.getId()))
                .map(this::toRuleResultResponse)
                .toList();

        return new ReportResponse(
                report.getId(),
                report.isPassed(),
                report.getTotalRules(),
                report.getTotalErrors(),
                report.getCreatedAt(),
                report.getUploadedFile().getOriginalFilename(),
                results);
    }

    private RuleResultResponse toRuleResultResponse(ValidationResult result) {
        return new RuleResultResponse(
                result.getId(),
                result.getRuleName(),
                result.isPassed(),
                result.getErrorCount(),
                result.getErrorMessages());
    }

    private ReportSummary toSummary(ValidationReport report) {
        return new ReportSummary(
                report.getId(),
                report.getUploadedFile().getOriginalFilename(),
                report.isPassed(),
                report.getTotalRules(),
                report.getTotalErrors(),
                report.getCreatedAt());
    }
}
