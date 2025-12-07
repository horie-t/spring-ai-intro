package com.example.spring_ai_demo.adapter.in.web;

import com.example.spring_ai_demo.adapter.in.web.dto.*;
import com.example.spring_ai_demo.adapter.out.persistence.AttachmentStore;
import com.example.spring_ai_demo.adapter.out.saas.OpenAIChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class ChatController {
    private final OpenAIChatService chatService;
    private final Logger logger = LoggerFactory.getLogger(ChatController.class);

    public ChatController(OpenAIChatService chatService) {
        this.chatService = chatService;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/chat")
    public AssistantUIChatModelRunResult chat(@RequestBody AssistantUIThreadMessage message) {
        String messageContent = message.getContent().getFirst().getText();
        if (message.getAttachments().isEmpty()) {
            logger.info("No attachments provided, returning text result");
            String resultText = chatService.withPrompt(new Prompt(new UserMessage(messageContent)));
            return new AssistantUIChatModelRunResult(
                    List.of(new AssistantUITextMessagePart(resultText))
            );
        } else {
            logger.info("Attachments provided, returning file result");
            AssistantUICompleteAttachment attachment = message.getAttachments().getFirst();

            return chatService.withPrompt(messageContent, attachment);
        }
    }
}
