package com.example.spring_ai_demo.adapter.in.web;

import com.example.spring_ai_demo.adapter.out.saas.OpenAIChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {
    private final OpenAIChatService chatService;

    public ChatController(OpenAIChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/api/chat")
    public Flux<String> chat(@RequestBody String userMessage) {
        return chatService.withUserMessage(userMessage);
    }
}
