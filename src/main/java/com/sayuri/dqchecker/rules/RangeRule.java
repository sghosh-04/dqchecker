package com.sayuri.dqchecker.rules;

import com.sayuri.dqchecker.model.RuleResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RangeRule extends Rule {

    private String columnName;
    private double min;
    private double max;

    public RangeRule(String columnName, double min, double max) {
        super("Range Check on " + columnName);
        this.columnName = columnName;
        this.min = min;
        this.max = max;
    }

    @Override
    public RuleResult validate(List<Map<String, String>> rows) {

        List<String> errors = new ArrayList<>();
        int rowNumber = 1;

        for (Map<String, String> row : rows) {
            String value = row.get(columnName);

            try {
                double num = Double.parseDouble(value);

                if (num < min || num > max) {
                    errors.add("Row " + rowNumber + ": " + columnName + " out of range");
                }

            } catch (Exception e) {
                errors.add("Row " + rowNumber + ": invalid number format");
            }

            rowNumber++;
        }

        boolean passed = errors.isEmpty();

        return new RuleResult(ruleName, passed, errors.size(), errors);
    }
}