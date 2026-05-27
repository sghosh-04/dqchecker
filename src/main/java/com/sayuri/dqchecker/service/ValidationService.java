package com.sayuri.dqchecker.service;

import com.sayuri.dqchecker.dto.ValidationResponse;
import com.sayuri.dqchecker.engine.DataQualityEngine;
import com.sayuri.dqchecker.entity.UploadedFile;
import com.sayuri.dqchecker.entity.ValidationReport;
import com.sayuri.dqchecker.entity.ValidationRule;
import com.sayuri.dqchecker.model.QualityReport;
import com.sayuri.dqchecker.model.RuleResult;
import com.sayuri.dqchecker.repository.ValidationReportRepository;
import com.sayuri.dqchecker.rules.DuplicateRule;
import com.sayuri.dqchecker.rules.NullCheckRule;
import com.sayuri.dqchecker.rules.RangeRule;
import com.sayuri.dqchecker.rules.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class ValidationService {

    private static final Logger log = LoggerFactory.getLogger(ValidationService.class);

    private final FileStorageService fileStorageService;
    private final ValidationReportRepository validationReportRepository;

    public ValidationService(FileStorageService fileStorageService, ValidationReportRepository validationReportRepository) {
        this.fileStorageService = fileStorageService;
        this.validationReportRepository = validationReportRepository;
    }

    public ValidationResponse validate(MultipartFile file, String userEmail) {
        UploadedFile uploadedFile = fileStorageService.saveMetadata(file, userEmail);
        List<Map<String, String>> rows = fileStorageService.parseCsv(file);
        QualityReport qualityReport = runDefaultRules(rows);
        ValidationReport savedReport = saveReport(uploadedFile, qualityReport);

        log.info("Validated upload id={} report id={} totalErrors={}",
                uploadedFile.getId(), savedReport.getId(), savedReport.getTotalErrors());

        return new ValidationResponse(
                savedReport.getId(),
                savedReport.isPassed(),
                savedReport.getTotalRules(),
                savedReport.getTotalErrors());
    }

    public QualityReport validateRows(List<Map<String, String>> rows) {
        log.info("Running validation against {} rows", rows == null ? 0 : rows.size());
        return runDefaultRules(rows);
    }

    private QualityReport runDefaultRules(List<Map<String, String>> rows) {
        List<Rule> rules = List.of(
                new NullCheckRule("name"),
                new RangeRule("age", 0, 100),
                new DuplicateRule("email")
        );
        return new DataQualityEngine(rules).run(rows);
    }

    private ValidationReport saveReport(UploadedFile uploadedFile, QualityReport qualityReport) {
        ValidationReport validationReport = new ValidationReport();
        validationReport.setUploadedFile(uploadedFile);
        validationReport.setTotalRules(qualityReport.getResults().size());
        validationReport.setTotalErrors(qualityReport.getResults().stream().mapToInt(RuleResult::getErrorCount).sum());
        validationReport.setPassed(validationReport.getTotalErrors() == 0);

        for (RuleResult result : qualityReport.getResults()) {
            ValidationRule validationRule = new ValidationRule();
            validationRule.setRuleName(result.getRuleName());
            validationRule.setPassed(result.isPassed());
            validationRule.setErrorCount(result.getErrorCount());
            validationRule.setErrorMessages(result.getErrorMessages());
            validationRule.setValidationReport(validationReport);
            validationReport.getRules().add(validationRule);
        }

        return validationReportRepository.save(validationReport);
    }
}
