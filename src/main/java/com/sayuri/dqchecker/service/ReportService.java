package com.sayuri.dqchecker.service;

import com.sayuri.dqchecker.dto.ReportResponse;
import com.sayuri.dqchecker.dto.RuleResultResponse;
import com.sayuri.dqchecker.entity.ValidationReport;
import com.sayuri.dqchecker.exception.ResourceNotFoundException;
import com.sayuri.dqchecker.repository.ValidationReportRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ReportService {

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

    public ReportResponse getReport(Long id, String email, boolean admin) {
        ValidationReport report = validationReportRepository.findWithRulesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        if (!admin && !report.getUploadedFile().getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("Report does not belong to current user");
        }
        return toResponse(report);
    }

    private ReportResponse toResponse(ValidationReport report) {
        List<RuleResultResponse> rules = report.getRules().stream()
                .sorted(Comparator.comparing(rule -> rule.getId() == null ? 0L : rule.getId()))
                .map(rule -> new RuleResultResponse(
                        rule.getId(),
                        rule.getRuleName(),
                        rule.isPassed(),
                        rule.getErrorCount(),
                        rule.getErrorMessages()))
                .toList();

        return new ReportResponse(
                report.getId(),
                report.isPassed(),
                report.getTotalRules(),
                report.getTotalErrors(),
                report.getCreatedAt(),
                report.getUploadedFile().getOriginalFilename(),
                rules);
    }
}
