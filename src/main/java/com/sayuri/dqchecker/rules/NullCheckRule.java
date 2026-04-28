package com.sayuri.dqchecker.rules;

import com.sayuri.dqchecker.model.RuleResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NullCheckRule extends Rule {

    private String columnName;

    public NullCheckRule(String columnName) {
        super("Null Check on " + columnName);
        this.columnName = columnName;
    }

    @Override
    public RuleResult validate(List<Map<String, String>> rows) {

        List<String> errors = new ArrayList<>();
        int rowNumber = 1;

        for (Map<String, String> row : rows) {
            String value = row.get(columnName);

            if (value == null || value.trim().isEmpty()) {
                errors.add("Row " + rowNumber + ": " + columnName + " is null/empty");
            }

            rowNumber++;
        }

        boolean passed = errors.isEmpty();

        return new RuleResult(ruleName, passed, errors.size(), errors);
    }
}