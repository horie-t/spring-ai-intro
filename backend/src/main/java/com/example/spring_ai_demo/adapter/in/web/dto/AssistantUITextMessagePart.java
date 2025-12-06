package com.example.spring_ai_demo.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AssistantUITextMessagePart implements AssistantUIThreadAssistantMessagePart {
    private String text;
}
