package com.example.spring_ai_demo.adapter.in.web;

import com.example.spring_ai_demo.adapter.in.web.dto.*;
import com.example.spring_ai_demo.adapter.out.persistence.AttachmentStore;
import com.example.spring_ai_demo.adapter.out.saas.OpenAIChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class ChatController {
    private final OpenAIChatService chatService;
    private final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final AttachmentStore attachmentStore;

    private static final String fileProcessingTemplate = """
            {messageContent}
            ---
            fileId: {fileId}
            fileName: {fileName}
            """;

    public ChatController(OpenAIChatService chatService, AttachmentStore attachmentStore) {
        this.chatService = chatService;
        this.attachmentStore = attachmentStore;
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
            String fileId = attachment.getId();
            String filename = attachment.getName();

            attachmentStore.addAttachment(fileId, attachment);

            String fileProcessingPrompt = PromptTemplate.builder()
                    .template(fileProcessingTemplate)
                    .variables(Map.of("fileId", fileId, "fileName", filename, "messageContent", messageContent)).build().render();
            String resultText = chatService.withPrompt(new Prompt(new UserMessage(fileProcessingPrompt)));

            // 一時処理
            attachmentStore.addAttachment(fileId + "_result", new AssistantUICompleteAttachment(
                    fileId + "_result",
                    "document",
                    filename,
                    "text/plain",
                    List.of(new AssistantUITextMessagePart("hoge"))
            ));

            AssistantUICompleteAttachment resultAttachment = attachmentStore.getAttachment(fileId + "_result");
            AssistantUIThreadUserMessagePart firstContent = resultAttachment.getContent().getFirst();
            String data = switch (firstContent) {
                case AssistantUITextMessagePart textMessagePart -> textMessagePart.getText();
                default -> "";
            };
            String mimeType = attachment.getContentType();
            return new AssistantUIChatModelRunResult(
                    List.of(new AssistantUITextMessagePart(resultText),
                            new AssistantUIFileMessagePart(Optional.of(filename), data, mimeType)
            ));
        }
    }
}
