package com.sayuri.dqchecker.service;

import com.sayuri.dqchecker.entity.Role;
import com.sayuri.dqchecker.entity.UploadedFile;
import com.sayuri.dqchecker.entity.User;
import com.sayuri.dqchecker.exception.InvalidFileException;
import com.sayuri.dqchecker.repository.UploadedFileRepository;
import com.sayuri.dqchecker.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    @Mock
    private UploadedFileRepository uploadedFileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FileStorageService fileStorageService;

    @Test
    void parseFileReturnsCsvRows() {
        MockMultipartFile file = csvFile();

        List<Map<String, String>> rows = fileStorageService.parseFile(file);

        assertEquals(2, rows.size());
        assertEquals("Ana", rows.get(0).get("name"));
    }

    @Test
    void parseFileReturnsExcelRows() {
        MockMultipartFile file = excelFile();

        List<Map<String, String>> rows = fileStorageService.parseFile(file);

        assertEquals(2, rows.size());
        assertEquals("Ana", rows.get(0).get("name"));
        assertEquals("20", rows.get(0).get("age"));
    }

    @Test
    void saveMetadataPersistsUploadOwner() {
        MockMultipartFile file = csvFile();
        when(userRepository.findByEmail("sayuri@example.com")).thenReturn(Optional.of(user()));
        when(uploadedFileRepository.save(any(UploadedFile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UploadedFile saved = fileStorageService.saveMetadata(file, "sayuri@example.com");

        assertEquals("customers.csv", saved.getOriginalFilename());
        assertEquals("sayuri@example.com", saved.getUser().getEmail());
    }

    @Test
    void rejectsInvalidFileFormat() {
        MockMultipartFile file = new MockMultipartFile("file", "data.txt", "text/plain", "x".getBytes());

        assertThrows(InvalidFileException.class, () -> fileStorageService.parseFile(file));
    }

    private MockMultipartFile csvFile() {
        return new MockMultipartFile(
                "file",
                "customers.csv",
                "text/csv",
                "name,age,email\nAna,20,a@example.com\nBob,30,b@example.com\n".getBytes());
    }

    private MockMultipartFile excelFile() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet();
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("name");
            header.createCell(1).setCellValue("age");
            header.createCell(2).setCellValue("email");

            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("Ana");
            row1.createCell(1).setCellValue(20);
            row1.createCell(2).setCellValue("a@example.com");

            Row row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue("Bob");
            row2.createCell(1).setCellValue(30);
            row2.createCell(2).setCellValue("b@example.com");

            workbook.write(out);
            return new MockMultipartFile(
                    "file",
                    "customers.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private User user() {
        User user = new User();
        user.setUsername("sayuri");
        user.setEmail("sayuri@example.com");
        user.setPassword("encoded");
        user.setRole(Role.USER);
        return user;
    }
}
