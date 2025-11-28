package com.example.spring_ai_demo.adapter.out.saas;

import jakarta.annotation.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class OpenAIChatService {
    private final ApplicationContext context;

    public OpenAIChatService(ApplicationContext context) {
        this.context = context;
    }

    @Nullable
    public Flux<String> withUserMessage(String userMessage) {
        ChatModel model = context.getBean(ChatModel.class);
        ChatClient client = ChatClient.create(model);
        return client.prompt(userMessage).stream().content();
    }
}
