package com.sayuri.dqchecker.model;

import java.util.List;

public class RuleResult {

    private String ruleName;
    private boolean passed;
    private int errorCount;
    private List<String> errorMessages;

    public RuleResult(String ruleName, boolean passed, int errorCount, List<String> errorMessages) {
        this.ruleName = ruleName;
        this.passed = passed;
        this.errorCount = errorCount;
        this.errorMessages = errorMessages;
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