package com.example.spring_ai_demo.adapter.out.persistence;

import com.example.spring_ai_demo.adapter.out.persistence.dto.DocumentSearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class RagSearchTool {
    private final Logger logger = LoggerFactory.getLogger(RagSearchTool.class);

    private final VectorStore vectorStore;

    public RagSearchTool(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Tool(description = "社内の最新ドキュメントやマニュアルから情報を検索します。")
    public String searchInternalDocuments(DocumentSearchRequest request, ToolContext context) {
        logger.info("searchInternalDocuments: {}", request);

        String username = context.getContext().get("username").toString();

        SearchRequest searchRequest = SearchRequest.builder()
                .query(request.query())
                .topK(3)
                .filterExpression("owner == '" + username + "'")
                .build();

        return vectorStore.similaritySearch(searchRequest).stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));
    }
}