package com.sayuri.dqchecker.engine;

import com.sayuri.dqchecker.model.QualityReport;
import com.sayuri.dqchecker.model.RuleResult;
import com.sayuri.dqchecker.rules.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataQualityEngine {

    private List<Rule> rules;

    public DataQualityEngine(List<Rule> rules) {
        this.rules = rules;
    }

    public QualityReport run(List<Map<String, String>> rows) {

        List<RuleResult> results = new ArrayList<>();

        for (Rule rule : rules) {
            RuleResult result = rule.validate(rows);
            results.add(result);
        }

        return new QualityReport(results);
    }
}