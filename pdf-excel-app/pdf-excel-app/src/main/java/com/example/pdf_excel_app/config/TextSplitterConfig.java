package com.example.pdf_excel_app.config;

import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TextSplitterConfig {

    // Configure TokenTextSplitter as a Spring Bean
    @Bean
    public TokenTextSplitter tokenTextSplitter() {

        return TokenTextSplitter.builder()

                // Maximum chunk size
                .withChunkSize(500)

                // Minimum chunk size in characters
                .withMinChunkSizeChars(100)

                // Minimum chunk length required for embedding
                .withMinChunkLengthToEmbed(10)

                // Maximum number of chunks
                .withMaxNumChunks(10000)

                // Preserve separators during splitting
                .withKeepSeparator(true)

                .build();
    }
}