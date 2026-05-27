package com.sayuri.dqchecker.dto;

public class ValidationResponse {

    private Long reportId;
    private boolean passed;
    private int totalRules;
    private int totalErrors;

    public ValidationResponse() {
    }

    public ValidationResponse(Long reportId, boolean passed, int totalRules, int totalErrors) {
        this.reportId = reportId;
        this.passed = passed;
        this.totalRules = totalRules;
        this.totalErrors = totalErrors;
    }

    public Long getReportId() {
        return reportId;
    }

    public boolean isPassed() {
        return passed;
    }

    public int getTotalRules() {
        return totalRules;
    }

    public int getTotalErrors() {
        return totalErrors;
    }
}
