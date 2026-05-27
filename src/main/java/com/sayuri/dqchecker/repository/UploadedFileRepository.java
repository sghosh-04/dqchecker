package com.sayuri.dqchecker.repository;

import com.sayuri.dqchecker.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
}
