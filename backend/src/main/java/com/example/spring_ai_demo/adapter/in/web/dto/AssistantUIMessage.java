package com.example.spring_ai_demo.adapter.in.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class AssistantUIMessage {
    private String role;
    private List<AssistantUIContent> content;

}
