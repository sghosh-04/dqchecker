package com.sayuri.dqchecker.service;

import com.sayuri.dqchecker.dto.ReportResponse;
import com.sayuri.dqchecker.dto.ReportSummary;
import com.sayuri.dqchecker.entity.Role;
import com.sayuri.dqchecker.entity.UploadedFile;
import com.sayuri.dqchecker.entity.User;
import com.sayuri.dqchecker.entity.ValidationReport;
import com.sayuri.dqchecker.entity.ValidationRule;
import com.sayuri.dqchecker.exception.ResourceNotFoundException;
import com.sayuri.dqchecker.repository.ValidationReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ValidationReportRepository validationReportRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    void getReportReturnsOwnedReport() {
        ValidationReport report = sampleReport("sayuri@example.com");
        when(validationReportRepository.findWithRulesById(10L)).thenReturn(Optional.of(report));

        ReportResponse response = reportService.getReport(10L, "sayuri@example.com", false);

        assertEquals("customers.csv", response.getFilename());
        assertEquals(1, response.getRules().size());
    }

    @Test
    void getReportRejectsOtherUsersReport() {
        ValidationReport report = sampleReport("owner@example.com");
        when(validationReportRepository.findWithRulesById(10L)).thenReturn(Optional.of(report));

        assertThrows(AccessDeniedException.class,
                () -> reportService.getReport(10L, "intruder@example.com", false));
    }

    @Test
    void getReportThrowsWhenMissing() {
        when(validationReportRepository.findWithRulesById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reportService.getReport(10L, "sayuri@example.com", false));
    }

    @Test
    void getReportHistoryReturnsPagedSummaries() {
        ValidationReport report = sampleReport("sayuri@example.com");
        PageRequest pageable = PageRequest.of(0, 10);
        when(validationReportRepository.searchReports("sayuri@example.com", false, false, "customers", pageable))
                .thenReturn(new PageImpl<>(List.of(report), pageable, 1));

        Page<ReportSummary> page = reportService.getReportHistory(
                "sayuri@example.com", false, false, " customers ", pageable);

        assertEquals(1, page.getTotalElements());
        assertEquals("customers.csv", page.getContent().get(0).getFilename());
    }

    @Test
    void deleteReportDeletesOwnedReport() {
        ValidationReport report = sampleReport("sayuri@example.com");
        when(validationReportRepository.findWithRulesById(10L)).thenReturn(Optional.of(report));

        reportService.deleteReport(10L, "sayuri@example.com", false);

        verify(validationReportRepository).delete(report);
    }

    private ValidationReport sampleReport(String ownerEmail) {
        User user = new User();
        user.setUsername("owner");
        user.setEmail(ownerEmail);
        user.setPassword("hash");
        user.setRole(Role.USER);

        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setOriginalFilename("customers.csv");
        uploadedFile.setContentType("text/csv");
        uploadedFile.setSize(100);
        uploadedFile.setUser(user);

        ValidationReport report = new ValidationReport();
        report.setUploadedFile(uploadedFile);
        report.setPassed(false);
        report.setTotalRules(1);
        report.setTotalErrors(1);

        ValidationRule rule = new ValidationRule();
        rule.setRuleName("Null Check on name");
        rule.setPassed(false);
        rule.setErrorCount(1);
        rule.setValidationReport(report);
        report.getRules().add(rule);

        return report;
    }
}
