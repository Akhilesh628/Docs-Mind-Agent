package com.example.pdf_excel_app.loader;

import org.apache.poi.ss.usermodel.*;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class ExcelLoader {

    public Document load(MultipartFile file) throws IOException {
     // Validate uploaded file.
        if (file == null || file.isEmpty())
        {
            throw new IllegalArgumentException("Excel file is empty");
        }


        // Read original filename.
        String filename = file.getOriginalFilename();

        // Validate Excel extension.
        if (filename == null ||
                !(filename.toLowerCase().endsWith(".xlsx")
                        || filename.toLowerCase().endsWith(".xls"))) {
            throw new IllegalArgumentException(
                    "Only XLS and XLSX files are allowed"
            );
        }

        // StringBuilder collects extracted Excel content.
        StringBuilder extractedText = new StringBuilder();

        // Open Excel workbook.
        try (Workbook workbook =
                     WorkbookFactory.create(file.getInputStream())) {

            // Formatter converts cell values into readable text.
            DataFormatter formatter = new DataFormatter();

            // Loop through every sheet.
            for (Sheet sheet : workbook) {

                extractedText.append("\nSheet: ")
                        .append(sheet.getSheetName())
                        .append("\n");

                // Loop through every row.
                for (Row row : sheet) {

                    // Loop through every cell.
                    for (Cell cell : row) {

                        String cellValue = formatter.formatCellValue(cell);

                        extractedText.append(cellValue).append(" | ");
                    }

                    // Move to the next line after each Excel row.
                    extractedText.append("\n");
                }
            }
        }
        // Check whether any text was extracted.
        if (extractedText.toString().isBlank()) {
            throw new IllegalArgumentException(
                    "No readable data found in Excel file"
            );
        }


        // Store original Excel file information.
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", filename);
        metadata.put("fileType", "EXCEL");

        // Return Excel content as Spring AI Document.
        return new Document(extractedText.toString(), metadata);

    }
}
