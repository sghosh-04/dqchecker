package com.sayuri.dqchecker.service;

import com.sayuri.dqchecker.dto.ValidationResponse;
import com.sayuri.dqchecker.entity.UploadedFile;
import com.sayuri.dqchecker.entity.ValidationReport;
import com.sayuri.dqchecker.repository.ValidationReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationServiceTest {

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ValidationReportRepository validationReportRepository;

    @InjectMocks
    private ValidationService validationService;

    @Test
    void validateParsesRunsRulesAndReturnsSavedSummary() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "customers.csv",
                "text/csv",
                "name,age,email\n,200,a@example.com\nAna,20,a@example.com\n".getBytes());
        List<Map<String, String>> rows = List.of(
                Map.of("name", "", "age", "200", "email", "a@example.com"),
                Map.of("name", "Ana", "age", "20", "email", "a@example.com")
        );

        when(fileStorageService.saveMetadata(file, "sayuri@example.com")).thenReturn(new UploadedFile());
        when(fileStorageService.parseCsv(file)).thenReturn(rows);
        when(validationReportRepository.save(any(ValidationReport.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ValidationResponse response = validationService.validate(file, "sayuri@example.com");

        assertFalse(response.isPassed());
        assertEquals(3, response.getTotalRules());
        assertEquals(3, response.getTotalErrors());
    }
}
