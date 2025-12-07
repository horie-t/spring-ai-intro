package com.example.spring_ai_demo.adapter.out.saas;

import com.example.spring_ai_demo.adapter.in.web.dto.*;
import com.example.spring_ai_demo.adapter.out.persistence.AttachmentStore;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OpenAIChatService {
    private final ApplicationContext context;

    private final AttachmentStore attachmentStore;

    private final ChatMemory chatMemory;
    private final SyncMcpToolCallbackProvider syncMcpToolCallbackProvider;
    private final FileTranslator fileTranslator;

    private static final String fileProcessingTemplate = """
            {messageContent}
            ---
            fileId: {fileId}
            fileName: {fileName}
            """;

    public OpenAIChatService(ApplicationContext context, AttachmentStore attachmentStore, ChatMemory chatMemory, SyncMcpToolCallbackProvider syncMcpToolCallbackProvider, FileTranslator fileTranslator) {
        this.context = context;
        this.attachmentStore = attachmentStore;
        this.chatMemory = chatMemory;
        this.syncMcpToolCallbackProvider = syncMcpToolCallbackProvider;
        this.fileTranslator = fileTranslator;
    }

    public String withUserMessage(String userMessage) {
        ChatModel model = context.getBean(ChatModel.class);
        ChatClient client = ChatClient.create(model);
        return client.prompt(userMessage).call().content();
    }

    public String withPrompt(Prompt prompt) {
        ChatModel model = context.getBean(ChatModel.class);
        ChatClient client = ChatClient.builder(model)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultToolCallbacks(syncMcpToolCallbackProvider).build();
        return client.prompt(prompt).call().content();
    }

    public AssistantUIChatModelRunResult withPrompt(String messageContent, AssistantUICompleteAttachment attachment) {
        String fileId = attachment.getId();
        String filename = attachment.getName();

        attachmentStore.addAttachment(fileId, attachment);
        String fileProcessingPrompt = PromptTemplate.builder()
                .template(fileProcessingTemplate)
                .variables(Map.of("fileId", fileId, "fileName", filename, "messageContent", messageContent)).build().render();

        ChatModel model = context.getBean(ChatModel.class);
        ChatClient client = ChatClient.builder(model)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultTools(fileTranslator)
                .defaultToolCallbacks(syncMcpToolCallbackProvider).build();
        String resultText = client.prompt(fileProcessingPrompt).call().content();

        AssistantUICompleteAttachment resultAttachment = attachmentStore.getAttachment(fileId + "_result");
        AssistantUIThreadUserMessagePart firstContent = resultAttachment.getContent().getFirst();
        String data = switch (firstContent) {
            case AssistantUITextMessagePart textMessagePart -> textMessagePart.getText();
            default -> "";
        };
        String mimeType = attachment.getContentType();
        String resultFilename = resultAttachment.getName();
        return new AssistantUIChatModelRunResult(
                List.of(new AssistantUITextMessagePart(resultText),
                        new AssistantUIFileMessagePart(Optional.of(resultFilename), data, mimeType)
                ));
    }

}