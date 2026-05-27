package com.sayuri.dqchecker.service;

import com.sayuri.dqchecker.dto.DashboardResponse;
import com.sayuri.dqchecker.repository.UploadedFileRepository;
import com.sayuri.dqchecker.repository.ValidationReportRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final UploadedFileRepository uploadedFileRepository;
    private final ValidationReportRepository validationReportRepository;

    public DashboardService(UploadedFileRepository uploadedFileRepository,
                            ValidationReportRepository validationReportRepository) {
        this.uploadedFileRepository = uploadedFileRepository;
        this.validationReportRepository = validationReportRepository;
    }

    public DashboardResponse getDashboard(String email, boolean admin) {
        long filesProcessed = admin ? uploadedFileRepository.count() : uploadedFileRepository.countByUserEmail(email);
        long reports = admin ? validationReportRepository.count() : validationReportRepository.countByUploadedFileUserEmail(email);
        long failures = admin
                ? validationReportRepository.countByPassedFalse()
                : validationReportRepository.countByUploadedFileUserEmailAndPassedFalse(email);
        int successRate = reports == 0 ? 0 : (int) Math.round(((reports - failures) * 100.0) / reports);
        return new DashboardResponse(filesProcessed, failures, successRate);
    }
}
