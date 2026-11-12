package com.example.pdf_excel_app.controller;

import com.example.pdf_excel_app.service.DocumentService;

import org.springframework.ai.document.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "http://localhost:5173")
public class DocumentController {

    private final DocumentService documentService;

    // Constructor injection
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    // Upload PDF or Excel file
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, Object>> uploadDocument(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        // Process document and generate chunks
        List<Document> chunks =
                documentService.processDocument(file);

        // Prepare API response
        Map<String, Object> response = new HashMap<>();

        response.put("message", "Document uploaded and processed successfully");
        response.put("fileName", file.getOriginalFilename());
        response.put("totalChunks", chunks.size());
        response.put("chunks", chunks);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
