package com.sayuri.dqchecker.service;

import com.sayuri.dqchecker.dto.DashboardResponse;
import com.sayuri.dqchecker.repository.UploadedFileRepository;
import com.sayuri.dqchecker.repository.ValidationReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private UploadedFileRepository uploadedFileRepository;

    @Mock
    private ValidationReportRepository validationReportRepository;

    @Test
    void getDashboardCalculatesUserMetrics() {
        DashboardService dashboardService = new DashboardService(uploadedFileRepository, validationReportRepository);
        when(uploadedFileRepository.countByUserEmail("sayuri@example.com")).thenReturn(5L);
        when(validationReportRepository.countByUploadedFileUserEmail("sayuri@example.com")).thenReturn(4L);
        when(validationReportRepository.countByUploadedFileUserEmailAndPassedFalse("sayuri@example.com")).thenReturn(1L);

        DashboardResponse response = dashboardService.getDashboard("sayuri@example.com", false);

        assertEquals(5, response.getFilesProcessed());
        assertEquals(1, response.getValidationFailures());
        assertEquals(75, response.getSuccessRate());
    }

    @Test
    void getDashboardReturnsZeroSuccessRateWithoutReports() {
        DashboardService dashboardService = new DashboardService(uploadedFileRepository, validationReportRepository);
        when(uploadedFileRepository.count()).thenReturn(2L);
        when(validationReportRepository.count()).thenReturn(0L);
        when(validationReportRepository.countByPassedFalse()).thenReturn(0L);

        DashboardResponse response = dashboardService.getDashboard("admin@example.com", true);

        assertEquals(2, response.getFilesProcessed());
        assertEquals(0, response.getValidationFailures());
        assertEquals(0, response.getSuccessRate());
    }
}
