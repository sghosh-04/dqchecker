package com.sayuri.dqchecker.dto;

public class RuleConfigRequest {
    private String type; // NULL_CHECK, RANGE_CHECK, DUPLICATE_CHECK
    private String columnName;
    private Double min;
    private Double max;

    public RuleConfigRequest() {
    }

    public RuleConfigRequest(String type, String columnName, Double min, Double max) {
        this.type = type;
        this.columnName = columnName;
        this.min = min;
        this.max = max;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }
}
