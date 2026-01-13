package com.example.spring_ai_demo.adapter.in.web;

import com.example.petstore.client.api.PetApi;
import com.example.petstore.client.model.Pet;
import com.example.spring_ai_demo.adapter.in.web.dto.*;
import com.example.spring_ai_demo.adapter.out.saas.OpenAIChatService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public AssistantUIChatModelRunResult chat(@RequestBody AssistantUIThreadMessage message) {
        AssistantUITextMessagePart resultTextMessage = chatService.withPrompt(new Prompt(new UserMessage(message.getContent().getFirst().getText())));
        return new AssistantUIChatModelRunResult(
                List.of(resultTextMessage)
        );
    }

    @PostMapping("/api/chat-en")
    public AssistantUIChatModelRunResult chat_en(@RequestBody String message) {
        AssistantUITextMessagePart resultTextMessage = chatService.searchInEnglish(new Prompt(new UserMessage(message)));
        return new AssistantUIChatModelRunResult(
                List.of(resultTextMessage)
        );
    }

    @PostMapping("/api/chat-routing")
    public AssistantUIChatModelRunResult chat_routing(@RequestBody String message) {
        AssistantUITextMessagePart resultTextMessage = chatService.withRouting(new Prompt(new UserMessage(message)));
        return new AssistantUIChatModelRunResult(
                List.of(resultTextMessage)
        );
    }

    @PostMapping("/api/chat-email-spear-phishing")
    public AssistantUIChatModelRunResult chat_email_threat(@RequestBody String message) {
        AssistantUITextMessagePart resultTextMessage = chatService.detectSpearPhishing(new Prompt(new UserMessage(message)));
        return new AssistantUIChatModelRunResult(
                List.of(resultTextMessage)
        );
    }

    @PostMapping("/api/chat-generate-report")
    public AssistantUIChatModelRunResult chat_generate_report(@RequestBody String message) {
        AssistantUITextMessagePart resultTextMessage = chatService.generateComprehensiveReport(new Prompt(new UserMessage(message)));
        return new AssistantUIChatModelRunResult(
                List.of(resultTextMessage)
        );
    }

    @PostMapping("/api/chat-translating")
    public AssistantUIChatModelRunResult chat_translating(@RequestBody String message) {
        AssistantUITextMessagePart resultTextMessage = chatService.translateWithRevision(new Prompt(new UserMessage(message)));
        return new AssistantUIChatModelRunResult(
                List.of(resultTextMessage)
        );
    }
}
