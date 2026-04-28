package com.sayuri.dqchecker.model;

import java.util.List;

public class QualityReport {

    private List<RuleResult> results;

    public QualityReport(List<RuleResult> results) {
        this.results = results;
    }

    public List<RuleResult> getResults() {
        return results;
    }
}