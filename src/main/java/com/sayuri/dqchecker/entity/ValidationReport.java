package com.sayuri.dqchecker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "validation_reports")
public class ValidationReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean passed;

    @Column(nullable = false)
    private int totalRules;

    @Column(nullable = false)
    private int totalErrors;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_file_id", nullable = false)
    private UploadedFile uploadedFile;

    @OneToMany(mappedBy = "validationReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ValidationRule> rules = new ArrayList<>();

    @OneToMany(mappedBy = "validationReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ValidationResult> results = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public int getTotalRules() {
        return totalRules;
    }

    public void setTotalRules(int totalRules) {
        this.totalRules = totalRules;
    }

    public int getTotalErrors() {
        return totalErrors;
    }

    public void setTotalErrors(int totalErrors) {
        this.totalErrors = totalErrors;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public List<ValidationRule> getRules() {
        return rules;
    }

    public List<ValidationResult> getResults() {
        return results;
    }
}
