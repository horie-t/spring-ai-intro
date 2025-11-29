package com.example.spring_ai_demo.adapter.in.web;

import com.example.spring_ai_demo.adapter.in.web.dto.AssistantUIChatRequest;
import com.example.spring_ai_demo.adapter.in.web.dto.AssistantUIContent;
import com.example.spring_ai_demo.adapter.out.saas.OpenAIChatService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ChatController {
    private final OpenAIChatService chatService;

    public ChatController(OpenAIChatService chatService) {
        this.chatService = chatService;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/chat")
    public Mono<AssistantUIContent> chat(@RequestBody AssistantUIChatRequest request) {
        System.out.println("chat called");
        System.out.println(request);
        String lastUserMessage = request.getMessages().getLast().getContent().getFirst().getText();
        return chatService.withUserMessage(lastUserMessage).map(AssistantUIContent::new);
    }
}
