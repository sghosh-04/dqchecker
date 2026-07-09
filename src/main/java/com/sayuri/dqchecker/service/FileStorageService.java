package com.sayuri.dqchecker.service;

import com.sayuri.dqchecker.entity.UploadedFile;
import com.sayuri.dqchecker.entity.User;
import com.sayuri.dqchecker.exception.FileStorageException;
import com.sayuri.dqchecker.exception.InvalidFileException;
import com.sayuri.dqchecker.repository.UploadedFileRepository;
import com.sayuri.dqchecker.repository.UserRepository;
import com.sayuri.dqchecker.util.DataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.poi.ss.usermodel.*;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private final UploadedFileRepository uploadedFileRepository;
    private final UserRepository userRepository;

    public FileStorageService(UploadedFileRepository uploadedFileRepository, UserRepository userRepository) {
        this.uploadedFileRepository = uploadedFileRepository;
        this.userRepository = userRepository;
    }

    public UploadedFile saveMetadata(MultipartFile file, String userEmail) {
        validateFile(file);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new InvalidFileException("Authenticated user was not found"));

        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setOriginalFilename(file.getOriginalFilename() == null ? "upload.csv" : file.getOriginalFilename());
        uploadedFile.setContentType(file.getContentType());
        uploadedFile.setSize(file.getSize());
        uploadedFile.setUser(user);

        UploadedFile saved = uploadedFileRepository.save(uploadedFile);
        log.info("Stored upload metadata id={} filename={} user={}", saved.getId(), saved.getOriginalFilename(), userEmail);
        return saved;
    }

    public List<Map<String, String>> parseFile(MultipartFile file) {
        validateFile(file);
        String filename = file.getOriginalFilename();
        try {
            if (filename != null && (filename.toLowerCase().endsWith(".xlsx") || filename.toLowerCase().endsWith(".xls"))) {
                return DataLoader.loadExcel(file.getInputStream());
            } else {
                return DataLoader.loadCSV(file.getInputStream());
            }
        } catch (IOException ex) {
            throw new FileStorageException("Unable to read uploaded file", ex);
        }
    }

    public List<String> parseHeaders(MultipartFile file) {
        validateFile(file);
        String filename = file.getOriginalFilename();
        if (filename != null && (filename.toLowerCase().endsWith(".xlsx") || filename.toLowerCase().endsWith(".xls"))) {
            try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
                if (workbook.getNumberOfSheets() == 0) {
                    return Collections.emptyList();
                }
                Sheet sheet = workbook.getSheetAt(0);
                Row headerRow = sheet.getRow(0);
                if (headerRow == null) {
                    return Collections.emptyList();
                }
                List<String> headers = new ArrayList<>();
                for (Cell cell : headerRow) {
                    headers.add(DataLoader.getCellValueAsString(cell).trim());
                }
                return headers;
            } catch (Exception e) {
                throw new InvalidFileException("Unable to read Excel headers", e);
            }
        } else {
            try (InputStreamReader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
                 CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
                return csvParser.getHeaderNames();
            } catch (IOException e) {
                throw new InvalidFileException("Unable to read CSV headers", e);
            }
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File is required");
        }
        String filename = file.getOriginalFilename();
        if (filename != null) {
            String lower = filename.toLowerCase();
            if (!lower.endsWith(".csv") && !lower.endsWith(".xls") && !lower.endsWith(".xlsx")) {
                throw new InvalidFileException("Only CSV and Excel files are supported");
            }
        }
    }
}
