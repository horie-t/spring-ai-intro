package com.example.spring_ai_demo.adapter.out.saas;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class OpenAIChatService {
    private final ApplicationContext context;
    private final SyncMcpToolCallbackProvider syncMcpToolCallbackProvider;

    public OpenAIChatService(ApplicationContext context, SyncMcpToolCallbackProvider syncMcpToolCallbackProvider) {
        this.context = context;
        this.syncMcpToolCallbackProvider = syncMcpToolCallbackProvider;
    }

    public String withUserMessage(String userMessage) {
        ChatModel model = context.getBean(ChatModel.class);
        ChatClient client = ChatClient.create(model);
        return client.prompt(userMessage).call().content();
    }

    public String withPrompt(Prompt prompt) {
        ChatModel model = context.getBean(ChatModel.class);
        ChatClient client = ChatClient.builder(model)
                .defaultToolCallbacks(syncMcpToolCallbackProvider).build();
        return client.prompt(prompt).call().content();
    }
}