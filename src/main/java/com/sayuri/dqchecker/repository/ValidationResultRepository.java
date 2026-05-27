package com.sayuri.dqchecker.repository;

import com.sayuri.dqchecker.entity.ValidationResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValidationResultRepository extends JpaRepository<ValidationResult, Long> {
}
