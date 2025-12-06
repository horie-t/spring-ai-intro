package com.example.spring_ai_demo.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class AssistantUIChatModelRunResult {
    private List<AssistantUIThreadAssistantMessagePart> content;
}
