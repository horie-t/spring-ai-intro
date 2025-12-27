package com.example.spring_ai_demo.adapter.out.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFileAttributes;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RagIngestionService {
    private final Logger logger = LoggerFactory.getLogger(RagIngestionService.class);

    private final VectorStore vectorStore;

    public RagIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void ingestFile(File file) {
        try {
            TikaDocumentReader reader = new TikaDocumentReader(file.toURI().toString());
            List<Document> documents = reader.get();

            PosixFileAttributes attrs = Files.readAttributes(file.toPath(), PosixFileAttributes.class);
            String owner = attrs.owner().getName();

            TokenTextSplitter splitter = new TokenTextSplitter();
            List<Document> chunks = splitter.apply(documents);

            List<Document> metadataEnrichedChunks = chunks.stream()
                    .map(doc -> {
                        Map<String, Object> metadata = doc.getMetadata();
                        metadata.put("source", file.getName());
                        metadata.put("owner", owner);
                        metadata.put("permissions", attrs.permissions().toString());
                        return new Document(Objects.requireNonNull(doc.getText()), metadata);
                    })
                    .collect(Collectors.toList());

            vectorStore.add(metadataEnrichedChunks);

            logger.info("PGVectorへの登録完了: {}", file.getName());
        } catch (Exception e) {
            throw new RuntimeException("RAGへのインデックス登録に失敗しました: " + file.getName(), e);
        }
    }
}
