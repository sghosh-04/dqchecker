package com.sayuri.dqchecker.controller;

import com.sayuri.dqchecker.engine.DataQualityEngine;
import com.sayuri.dqchecker.model.QualityReport;
import com.sayuri.dqchecker.rules.*;
import com.sayuri.dqchecker.util.DataLoader;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DataQualityController {

    private List<Map<String, String>> data;

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            data = DataLoader.loadCSV(inputStream);
            return "File uploaded successfully!";
        } catch (Exception e) {
            return "Error uploading file";
        }
    }

    @PostMapping("/validate")
    public QualityReport validate() {

        List<Rule> rules = List.of(
                new NullCheckRule("name"),
                new RangeRule("age", 0, 100),
                new DuplicateRule("email")
        );

        DataQualityEngine engine = new DataQualityEngine(rules);

        return engine.run(data);
    }
}