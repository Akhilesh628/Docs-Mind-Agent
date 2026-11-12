package com.example.pdf_excel_app.service;

import com.example.pdf_excel_app.loader.ExcelLoader;
import com.example.pdf_excel_app.loader.PdfLoader;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ai.vectorstore.VectorStore;
import java.io.IOException;
import java.util.*;

@Service
public class DocumentService {
    private final PdfLoader pdfLoader;
    private final ExcelLoader excelLoader;
    private final TokenTextSplitter textSplitter;
    private final VectorStore vectorStore;

    public DocumentService(PdfLoader pdfLoader, ExcelLoader excelLoader, TokenTextSplitter textSplitter, VectorStore vectorStore) {
        this.pdfLoader = pdfLoader;
        this.excelLoader = excelLoader;
        this.textSplitter = textSplitter;
        this.vectorStore = vectorStore;
    }

    public List<Document> processDocument(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please upload a file");
        }
        String filename = file.getOriginalFilename();

        if (filename == null) {
            throw new IllegalArgumentException("File name is missing");
        }
        String lowerCaseFilename = filename.toLowerCase();
        // Step 1: Extract original document text.
        Document originalDocument;

        if (lowerCaseFilename.endsWith(".pdf")) {

            originalDocument = pdfLoader.loadPdf(file);

        } else if (lowerCaseFilename.endsWith(".xlsx")
                || lowerCaseFilename.endsWith(".xls")) {

            originalDocument = excelLoader.load(file);

        } else {

            throw new IllegalArgumentException(
                    "Only PDF, XLS and XLSX files are supported"
            );
        }
        // Step 2: Generate unique document ID.
        String documentId = UUID.randomUUID().toString();
        // Step 3: Add metadata.
        Map<String, Object> metadata =
                new HashMap<>(originalDocument.getMetadata());


        metadata.put("documentId", documentId);
        metadata.put("source", filename);

        Document documentWithMetadata =
                Document.builder()
                        .text(originalDocument.getText())
                        .metadata(metadata)
                        .build();

        // Step 4: Split document using injected TokenTextSplitter bean.
        List<Document> splitDocuments =
                textSplitter.apply(List.of(documentWithMetadata));
        // Step 5: Add chunk information.
        List<Document> finalChunks = new ArrayList<>();

        for (int i = 0; i < splitDocuments.size(); i++) {

            Document chunk = splitDocuments.get(i);

            Map<String, Object> chunkMetadata =
                    new HashMap<>(chunk.getMetadata());

            chunkMetadata.put("chunkIndex", i);
            chunkMetadata.put("totalChunks", splitDocuments.size());

            Document finalChunk =
                    Document.builder()
                            .text(chunk.getText())
                            .metadata(chunkMetadata)
                            .build();

            finalChunks.add(finalChunk);
        }
        // Step 6: Check chunks
        System.out.println("Chunks before saving: " + finalChunks.size());

        // Step 7: Save chunks into PostgreSQL PGVector
        vectorStore.add(finalChunks);

        System.out.println("Chunks saved successfully in PGVector");

        // Step 6: Return processed chunks.
        return finalChunks;
    }

}
