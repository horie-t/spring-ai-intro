package com.example.spring_ai_demo.adapter.in.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class AssistantUIChatRequest {
    private List<AssistantUIMessage> messages;
}
