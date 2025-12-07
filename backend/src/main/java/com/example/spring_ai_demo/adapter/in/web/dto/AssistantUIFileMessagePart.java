package com.example.spring_ai_demo.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@AllArgsConstructor
@Data
public class AssistantUIFileMessagePart implements AssistantUIThreadAssistantMessagePart {
    private Optional<String> filename;
    private String data;
    private String mimeType;
}
