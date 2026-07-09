package com.sayuri.dqchecker.service;

import com.sayuri.dqchecker.dto.ValidationResponse;
import com.sayuri.dqchecker.dto.RuleConfigRequest;
import com.sayuri.dqchecker.engine.DataQualityEngine;
import com.sayuri.dqchecker.entity.UploadedFile;
import com.sayuri.dqchecker.entity.ValidationReport;
import com.sayuri.dqchecker.entity.ValidationResult;
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

import java.util.ArrayList;
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
        List<RuleConfigRequest> defaultRules = List.of(
                new RuleConfigRequest("NULL_CHECK", "name", null, null),
                new RuleConfigRequest("RANGE_CHECK", "age", 0.0, 100.0),
                new RuleConfigRequest("DUPLICATE_CHECK", "email", null, null)
        );
        return validate(file, defaultRules, userEmail);
    }

    public ValidationResponse validate(MultipartFile file, List<RuleConfigRequest> ruleConfigs, String userEmail) {
        UploadedFile uploadedFile = fileStorageService.saveMetadata(file, userEmail);
        List<Map<String, String>> rows = fileStorageService.parseFile(file);
        QualityReport qualityReport = runCustomRules(rows, ruleConfigs);
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
        List<RuleConfigRequest> defaultRules = List.of(
                new RuleConfigRequest("NULL_CHECK", "name", null, null),
                new RuleConfigRequest("RANGE_CHECK", "age", 0.0, 100.0),
                new RuleConfigRequest("DUPLICATE_CHECK", "email", null, null)
        );
        return runCustomRules(rows, defaultRules);
    }

    private QualityReport runCustomRules(List<Map<String, String>> rows, List<RuleConfigRequest> ruleConfigs) {
        List<Rule> rules = new ArrayList<>();
        for (RuleConfigRequest config : ruleConfigs) {
            switch (config.getType().toUpperCase()) {
                case "NULL_CHECK":
                    rules.add(new NullCheckRule(config.getColumnName()));
                    break;
                case "DUPLICATE_CHECK":
                    rules.add(new DuplicateRule(config.getColumnName()));
                    break;
                case "RANGE_CHECK":
                    double min = config.getMin() != null ? config.getMin() : Double.MIN_VALUE;
                    double max = config.getMax() != null ? config.getMax() : Double.MAX_VALUE;
                    rules.add(new RangeRule(config.getColumnName(), min, max));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown rule type: " + config.getType());
            }
        }
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

            ValidationResult validationResult = new ValidationResult();
            validationResult.setRuleName(result.getRuleName());
            validationResult.setPassed(result.isPassed());
            validationResult.setErrorCount(result.getErrorCount());
            validationResult.setErrorMessages(result.getErrorMessages());
            validationResult.setValidationReport(validationReport);
            validationReport.getResults().add(validationResult);
        }

        return validationReportRepository.save(validationReport);
    }
}
