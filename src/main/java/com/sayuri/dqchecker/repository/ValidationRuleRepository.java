package com.sayuri.dqchecker.repository;

import com.sayuri.dqchecker.entity.ValidationRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValidationRuleRepository extends JpaRepository<ValidationRule, Long> {
}
