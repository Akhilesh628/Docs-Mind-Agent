package com.example.pdf_excel_app.controller;


import com.example.pdf_excel_app.service.RAGService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag")
@CrossOrigin(origins = "http://localhost:5173")
public class RAGController {

    private final RAGService ragService;

    public RAGController(RAGService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/ask")
    public String askQuestion(
            @RequestParam String question) {

        return ragService.askQuestion(question);
    }
}
