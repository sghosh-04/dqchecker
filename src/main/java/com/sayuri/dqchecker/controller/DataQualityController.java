package com.sayuri.dqchecker.controller;

import com.sayuri.dqchecker.dto.ReportResponse;
import com.sayuri.dqchecker.dto.ValidationResponse;
import com.sayuri.dqchecker.exception.InvalidFileException;
import com.sayuri.dqchecker.model.QualityReport;
import com.sayuri.dqchecker.service.FileStorageService;
import com.sayuri.dqchecker.service.ReportService;
import com.sayuri.dqchecker.service.ValidationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
public class DataQualityController {

    private List<Map<String, String>> data;

    private final ValidationService validationService;
    private final ReportService reportService;
    private final FileStorageService fileStorageService;

    public DataQualityController(ValidationService validationService, ReportService reportService,
                                 FileStorageService fileStorageService) {
        this.validationService = validationService;
        this.reportService = reportService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/api/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        data = fileStorageService.parseCsv(file);
        return "File uploaded successfully!";
    }

    @PostMapping("/api/validate")
    public QualityReport validate() {
        if (data == null) {
            throw new InvalidFileException("Upload a CSV file before validating");
        }
        return validationService.validateRows(data);
    }

    @PostMapping("/validate")
    public ValidationResponse validateFile(@RequestParam("file") MultipartFile file, Authentication authentication) {
        return validationService.validate(file, authentication.getName());
    }

    @GetMapping("/reports")
    public List<ReportResponse> reports(Authentication authentication) {
        return reportService.getReports(authentication.getName(), isAdmin(authentication));
    }

    @GetMapping("/reports/{id}")
    public ReportResponse report(@PathVariable Long id, Authentication authentication) {
        return reportService.getReport(id, authentication.getName(), isAdmin(authentication));
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }
}
