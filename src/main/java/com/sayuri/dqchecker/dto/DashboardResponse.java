package com.sayuri.dqchecker.dto;

public class DashboardResponse {

    private long filesProcessed;
    private long validationFailures;
    private int successRate;

    public DashboardResponse(long filesProcessed, long validationFailures, int successRate) {
        this.filesProcessed = filesProcessed;
        this.validationFailures = validationFailures;
        this.successRate = successRate;
    }

    public long getFilesProcessed() {
        return filesProcessed;
    }

    public long getValidationFailures() {
        return validationFailures;
    }

    public int getSuccessRate() {
        return successRate;
    }
}
