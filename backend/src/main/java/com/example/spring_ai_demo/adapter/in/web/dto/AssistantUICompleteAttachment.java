package com.example.spring_ai_demo.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class AssistantUICompleteAttachment {
    private String id;
    private String type;
    private String name;
    private String contentType;
    private List<AssistantUIThreadUserMessagePart> content;
}
