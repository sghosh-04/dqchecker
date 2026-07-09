package com.sayuri.dqchecker.controller;

import com.sayuri.dqchecker.dto.ReportResponse;
import com.sayuri.dqchecker.dto.ReportSummary;
import com.sayuri.dqchecker.dto.ValidationResponse;
import com.sayuri.dqchecker.dto.RuleConfigRequest;
import com.sayuri.dqchecker.exception.InvalidFileException;
import com.sayuri.dqchecker.model.QualityReport;
import com.sayuri.dqchecker.service.FileStorageService;
import com.sayuri.dqchecker.service.ReportService;
import com.sayuri.dqchecker.service.ValidationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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
    @Operation(summary = "Upload a CSV or Excel file for legacy in-memory validation")
    public String upload(@RequestParam("file") MultipartFile file) {
        data = fileStorageService.parseFile(file);
        return "File uploaded successfully!";
    }

    @PostMapping("/api/validate")
    @Operation(summary = "Run legacy in-memory validation")
    public QualityReport validate() {
        if (data == null) {
            throw new InvalidFileException("Upload a CSV or Excel file before validating");
        }
        return validationService.validateRows(data);
    }

    @PostMapping(value = "/api/parse-headers", consumes = "multipart/form-data")
    @Operation(summary = "Parse and return headers of CSV or Excel file")
    public List<String> parseHeaders(@RequestParam("file") MultipartFile file) {
        return fileStorageService.parseHeaders(file);
    }

    @PostMapping(value = "/validate", consumes = "multipart/form-data")
    @Operation(summary = "Upload, validate with custom rules, persist, and return a report id")
    public ValidationResponse validateFile(
            @RequestPart("file") MultipartFile file,
            @RequestPart("rules") List<RuleConfigRequest> rules,
            Authentication authentication) {
        return validationService.validate(file, rules, authentication.getName());
    }

    @GetMapping("/validate/{id}")
    @Operation(summary = "Get a persisted validation report")
    public ReportResponse validationReport(@PathVariable Long id, Authentication authentication) {
        return reportService.getReport(id, authentication.getName(), isAdmin(authentication));
    }

    @GetMapping("/reports")
    @Operation(summary = "Get report history with pagination and filtering")
    public Page<ReportSummary> reports(@RequestParam(required = false) Boolean passed,
                                       @RequestParam(required = false) String filename,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       Authentication authentication) {
        return reportService.getReportHistory(
                authentication.getName(),
                isAdmin(authentication),
                passed,
                filename,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @GetMapping("/reports/all")
    @Operation(summary = "Get all validation reports with rule details")
    public List<ReportResponse> reports(Authentication authentication) {
        return reportService.getReports(authentication.getName(), isAdmin(authentication));
    }

    @GetMapping("/reports/{id}")
    @Operation(summary = "Get a report by id")
    public ReportResponse report(@PathVariable Long id, Authentication authentication) {
        return reportService.getReport(id, authentication.getName(), isAdmin(authentication));
    }

    @DeleteMapping("/reports/{id}")
    @Operation(summary = "Delete a report by id")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id, Authentication authentication) {
        reportService.deleteReport(id, authentication.getName(), isAdmin(authentication));
        return ResponseEntity.noContent().build();
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }
}
