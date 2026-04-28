package com.sayuri.dqchecker.rules;

import com.sayuri.dqchecker.model.RuleResult;
import java.util.List;
import java.util.Map;

public abstract class Rule {

    protected String ruleName;

    public Rule(String ruleName) {
        this.ruleName = ruleName;
    }

    public abstract RuleResult validate(List<Map<String, String>> rows);
}