package com.sayuri.dqchecker.controller;

import com.sayuri.dqchecker.dto.ReportResponse;
import com.sayuri.dqchecker.dto.RuleConfigRequest;
import com.sayuri.dqchecker.dto.ValidationResponse;
import com.sayuri.dqchecker.exception.InvalidFileException;
import com.sayuri.dqchecker.model.QualityReport;
import com.sayuri.dqchecker.model.RuleResult;
import com.sayuri.dqchecker.service.FileStorageService;
import com.sayuri.dqchecker.service.ReportService;
import com.sayuri.dqchecker.service.ValidationService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DataQualityControllerTest {

    private final ValidationService validationService = mock(ValidationService.class);
    private final ReportService reportService = mock(ReportService.class);
    private final FileStorageService fileStorageService = mock(FileStorageService.class);
    private final DataQualityController controller =
            new DataQualityController(validationService, reportService, fileStorageService);

    @Test
    void rootValidateDelegatesToValidationService() {
        MockMultipartFile file = new MockMultipartFile("file", "customers.csv", "text/csv", "a,b\n1,2".getBytes());
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("sayuri@example.com", null);
        List<RuleConfigRequest> rules = java.util.Collections.emptyList();
        when(validationService.validate(file, rules, "sayuri@example.com"))
                .thenReturn(new ValidationResponse(10L, true, 3, 0));

        assertEquals(10L, controller.validateFile(file, rules, authentication).getReportId());
    }

    @Test
    void legacyUploadAndValidateStillWorks() {
        MockMultipartFile file = new MockMultipartFile("file", "customers.csv", "text/csv", "a,b\n1,2".getBytes());
        List<Map<String, String>> rows = List.of(Map.of("name", "Ana", "age", "20", "email", "a@example.com"));
        QualityReport report = new QualityReport(List.of(new RuleResult("ok", true, 0, List.of())));

        when(fileStorageService.parseFile(file)).thenReturn(rows);
        when(validationService.validateRows(rows)).thenReturn(report);

        assertEquals("File uploaded successfully!", controller.upload(file));
        assertEquals(report, controller.validate());
    }

    @Test
    void legacyValidateRequiresUpload() {
        DataQualityController fresh = new DataQualityController(validationService, reportService, fileStorageService);

        assertThrows(InvalidFileException.class, fresh::validate);
    }

    @Test
    void reportsDelegatesWithAdminFlag() {
        TestingAuthenticationToken authentication =
                new TestingAuthenticationToken("admin@example.com", null, "ROLE_ADMIN");
        List<ReportResponse> reports = List.of(new ReportResponse(1L, true, 3, 0, Instant.now(), "file.csv", List.of()));
        when(reportService.getReports("admin@example.com", true)).thenReturn(reports);

        assertEquals(1, controller.reports(authentication).size());
    }
}
