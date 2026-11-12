package com.example.pdf_excel_app.service;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RAGService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    // Constructor injection
    public RAGService(
            VectorStore vectorStore,
            ChatClient chatClient) {

        this.vectorStore = vectorStore;
        this.chatClient = chatClient;
    }

    // Ask question and retrieve information from uploaded files
    public String askQuestion(String question) {

        // Step 1: Search relevant chunks in PGVector
        List<Document> documents =
                vectorStore.similaritySearch(
                        SearchRequest.builder()
                                .query(question)
                                .topK(5)
                                .build()
                );

        // Step 2: Check whether relevant documents were found
        if (documents == null || documents.isEmpty()) {
            return "No relevant information found in uploaded documents.";
        }

        // Step 3: Combine retrieved document chunks
        String context = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        // Step 4: Send retrieved context and question to Groq
        // Step 4: Send context and question to Groq
        return chatClient.prompt()
                .system("""
                        You are an intelligent RAG document assistant.

                        Answer using only the provided document context.

                        IMPORTANT FORMATTING RULES:

                        1. Display structured information in Markdown tables.
                        2. Use proper table headings.
                        3. For employee information, use columns:
                           Field | Details
                        4. For multiple people, use separate rows.
                        5. Use bullet points only when a table is unsuitable.
                        6. Do not invent information.
                        7. If information is missing, write Not Available.
                        8. Keep answers clear and easy to understand.

                        DOCUMENT CONTEXT:
                        """ + context)

                .user(question)

                // Step 5: Generate AI response
                .call()
                .content();
    }
}
