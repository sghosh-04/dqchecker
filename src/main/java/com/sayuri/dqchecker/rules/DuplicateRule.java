package com.sayuri.dqchecker.rules;

import com.sayuri.dqchecker.model.RuleResult;

import java.util.*;

public class DuplicateRule extends Rule {

    private String columnName;

    public DuplicateRule(String columnName) {
        super("Duplicate Check on " + columnName);
        this.columnName = columnName;
    }

    @Override
    public RuleResult validate(List<Map<String, String>> rows) {

        Set<String> seen = new HashSet<>();
        List<String> errors = new ArrayList<>();
        int rowNumber = 1;

        for (Map<String, String> row : rows) {
            String value = row.get(columnName);

            if (value != null) {
                if (seen.contains(value)) {
                    errors.add("Row " + rowNumber + ": duplicate value '" + value + "'");
                } else {
                    seen.add(value);
                }
            }

            rowNumber++;
        }

        boolean passed = errors.isEmpty();

        return new RuleResult(ruleName, passed, errors.size(), errors);
    }
}