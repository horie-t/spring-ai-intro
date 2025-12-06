package com.example.spring_ai_demo.adapter.in.web;

import com.example.spring_ai_demo.adapter.in.web.dto.AssistantUIContent;
import com.example.spring_ai_demo.adapter.in.web.dto.AssistantUIMessage;
import com.example.spring_ai_demo.adapter.out.saas.OpenAIChatService;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
    private final OpenAIChatService chatService;

    public ChatController(OpenAIChatService chatService) {
        this.chatService = chatService;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/chat")
    public AssistantUIContent chat(@RequestBody AssistantUIMessage message) {
        return new AssistantUIContent(chatService.withPrompt(new Prompt(new UserMessage(message.getContent().getFirst().getText()))));
    }
}
