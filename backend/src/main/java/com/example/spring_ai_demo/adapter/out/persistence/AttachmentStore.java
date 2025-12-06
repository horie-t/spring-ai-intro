package com.example.spring_ai_demo.adapter.out.persistence;

import com.example.spring_ai_demo.adapter.in.web.dto.AssistantUICompleteAttachment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AttachmentStore {
    private final Map<String, AssistantUICompleteAttachment> attachments = new HashMap<>();

    public void addAttachment(String id, AssistantUICompleteAttachment attachment) {
        attachments.put(id, attachment);
    }

    public AssistantUICompleteAttachment getAttachment(String id) {
        return attachments.get(id);
    }

    public boolean hasAttachment(String id) {
        return attachments.containsKey(id);
    }
}
