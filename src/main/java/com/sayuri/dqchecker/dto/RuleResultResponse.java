package com.sayuri.dqchecker.dto;

import java.util.List;

public class RuleResultResponse {

    private Long id;
    private String ruleName;
    private boolean passed;
    private int errorCount;
    private List<String> errorMessages;

    public RuleResultResponse(Long id, String ruleName, boolean passed, int errorCount, List<String> errorMessages) {
        this.id = id;
        this.ruleName = ruleName;
        this.passed = passed;
        this.errorCount = errorCount;
        this.errorMessages = errorMessages;
    }

    public Long getId() {
        return id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public boolean isPassed() {
        return passed;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
