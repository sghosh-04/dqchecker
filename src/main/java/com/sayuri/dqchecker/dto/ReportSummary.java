package com.sayuri.dqchecker.dto;

import java.time.Instant;

public class ReportSummary {

    private Long id;
    private String filename;
    private boolean passed;
    private int totalRules;
    private int totalErrors;
    private Instant createdAt;

    public ReportSummary(Long id, String filename, boolean passed, int totalRules, int totalErrors, Instant createdAt) {
        this.id = id;
        this.filename = filename;
        this.passed = passed;
        this.totalRules = totalRules;
        this.totalErrors = totalErrors;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getFilename() {
        return filename;
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
}
