package com.sayuri.dqchecker.dto;

import java.time.Instant;
import java.util.List;

public class ReportResponse {

    private Long id;
    private boolean passed;
    private int totalRules;
    private int totalErrors;
    private Instant createdAt;
    private String filename;
    private List<RuleResultResponse> rules;

    public ReportResponse(Long id, boolean passed, int totalRules, int totalErrors, Instant createdAt,
                          String filename, List<RuleResultResponse> rules) {
        this.id = id;
        this.passed = passed;
        this.totalRules = totalRules;
        this.totalErrors = totalErrors;
        this.createdAt = createdAt;
        this.filename = filename;
        this.rules = rules;
    }

    public Long getId() {
        return id;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getFilename() {
        return filename;
    }

    public List<RuleResultResponse> getRules() {
        return rules;
    }
}
