package com.sayuri.dqchecker.util;

import com.sayuri.dqchecker.exception.InvalidFileException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DataLoader {

    public static List<Map<String, String>> loadCSV(InputStream inputStream) {

        List<Map<String, String>> data = new ArrayList<>();

        try (
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())
        ) {

            for (CSVRecord record : csvParser) {
                Map<String, String> row = new HashMap<>();

                for (String header : csvParser.getHeaderNames()) {
                    row.put(header, record.get(header));
                }

                data.add(row);
            }

        } catch (Exception e) {
            throw new InvalidFileException("Unable to parse CSV file", e);
        }

        return data;
    }

    public static List<Map<String, String>> loadExcel(InputStream inputStream) {
        List<Map<String, String>> data = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            if (workbook.getNumberOfSheets() == 0) {
                return data;
            }
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return data;
            }

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(getCellValueAsString(cell).trim());
            }

            int rowCount = sheet.getLastRowNum();
            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                Map<String, String> rowData = new HashMap<>();
                boolean hasData = false;
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    String val = getCellValueAsString(cell);
                    if (!val.isEmpty()) {
                        hasData = true;
                    }
                    rowData.put(headers.get(j), val);
                }
                if (hasData) {
                    data.add(rowData);
                }
            }

        } catch (Exception e) {
            throw new InvalidFileException("Unable to parse Excel file", e);
        }

        return data;
    }

    public static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                double numericValue = cell.getNumericCellValue();
                if (numericValue == (long) numericValue) {
                    return String.valueOf((long) numericValue);
                }
                return String.valueOf(numericValue);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    try {
                        double numericVal = cell.getNumericCellValue();
                        if (numericVal == (long) numericVal) {
                            return String.valueOf((long) numericVal);
                        }
                        return String.valueOf(numericVal);
                    } catch (Exception ex) {
                        return "";
                    }
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}
