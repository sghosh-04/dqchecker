package com.sayuri.dqchecker.service;

import com.sayuri.dqchecker.entity.UploadedFile;
import com.sayuri.dqchecker.entity.User;
import com.sayuri.dqchecker.exception.FileStorageException;
import com.sayuri.dqchecker.exception.InvalidCsvException;
import com.sayuri.dqchecker.repository.UploadedFileRepository;
import com.sayuri.dqchecker.repository.UserRepository;
import com.sayuri.dqchecker.util.DataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
        validateCsv(file);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new InvalidCsvException("Authenticated user was not found"));

        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setOriginalFilename(file.getOriginalFilename() == null ? "upload.csv" : file.getOriginalFilename());
        uploadedFile.setContentType(file.getContentType());
        uploadedFile.setSize(file.getSize());
        uploadedFile.setUser(user);

        UploadedFile saved = uploadedFileRepository.save(uploadedFile);
        log.info("Stored upload metadata id={} filename={} user={}", saved.getId(), saved.getOriginalFilename(), userEmail);
        return saved;
    }

    public List<Map<String, String>> parseCsv(MultipartFile file) {
        validateCsv(file);
        try {
            return DataLoader.loadCSV(file.getInputStream());
        } catch (IOException ex) {
            throw new FileStorageException("Unable to read uploaded file", ex);
        }
    }

    private void validateCsv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidCsvException("CSV file is required");
        }
        String filename = file.getOriginalFilename();
        if (filename != null && !filename.toLowerCase().endsWith(".csv")) {
            throw new InvalidCsvException("Only CSV files are supported");
        }
    }
}
