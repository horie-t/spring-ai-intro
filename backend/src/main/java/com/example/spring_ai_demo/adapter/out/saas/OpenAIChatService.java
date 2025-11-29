package com.example.spring_ai_demo.adapter.out.saas;

import jakarta.annotation.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
public class OpenAIChatService {
    private final ApplicationContext context;

    public OpenAIChatService(ApplicationContext context) {
        this.context = context;
    }

    @Nullable
    public Mono<String> withUserMessage(String userMessage) {
        ChatModel model = context.getBean(ChatModel.class);
        ChatClient client = ChatClient.create(model);
        return client.prompt(userMessage).stream().content().collect(Collectors.joining());
    }

    public Mono<String> withPrompt(Prompt prompt) {
        ChatModel model = context.getBean(ChatModel.class);
        ChatClient client = ChatClient.create(model);
        return client.prompt(prompt).stream().content().collect(Collectors.joining());
    }
}
