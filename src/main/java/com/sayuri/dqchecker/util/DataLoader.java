package com.sayuri.dqchecker.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DataLoader {

    public static List<Map<String, String>> loadCSV(InputStream inputStream) {

        List<Map<String, String>> data = new ArrayList<>();

        try (
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())
        ) {

            for (CSVRecord record : csvParser) {
                Map<String, String> row = new HashMap<>();

                for (String header : csvParser.getHeaderNames()) {
                    row.put(header, record.get(header));
                }

                data.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }
}