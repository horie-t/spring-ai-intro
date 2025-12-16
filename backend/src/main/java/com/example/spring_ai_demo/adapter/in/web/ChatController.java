package com.example.spring_ai_demo.adapter.in.web;

import com.example.spring_ai_demo.adapter.in.web.dto.*;
import com.example.spring_ai_demo.adapter.out.saas.OpenAIChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class ChatController {
    private final OpenAIChatService chatService;
    private final Logger logger = LoggerFactory.getLogger(ChatController.class);

    public ChatController(OpenAIChatService chatService) {
        this.chatService = chatService;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/chat")
    public AssistantUIChatModelRunResult chat(@RequestBody AssistantUIThreadMessage message, Principal principal) {
        logger.info("Principal: {}", principal);
        AssistantUITextMessagePart resultTextMessage = chatService.withPrompt(new Prompt(new UserMessage(message.getContent().getFirst().getText())));
        return new AssistantUIChatModelRunResult(
                List.of(resultTextMessage)
        );
    }
}
