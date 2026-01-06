package com.example.spring_ai_demo.adapter.out.saas;

import com.example.spring_ai_demo.adapter.in.web.dto.AssistantUITextMessagePart;
import com.example.spring_ai_demo.adapter.out.persistence.RagSearchTool;
import com.example.spring_ai_demo.adapter.out.saas.dto.AccountTitle;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Service
public class OpenAIChatService {
    private final ApplicationContext context;
    private final ChatMemory chatMemory;
    private final SyncMcpToolCallbackProvider syncMcpToolCallbackProvider;
    private final VectorStore vectorStore;
    private final Logger logger = LoggerFactory.getLogger(OpenAIChatService.class);

    private static final String classifyExpensesPromptTemplate = """
        以下の支出記録を勘定科目に分類してください。
        支出記録: {expense}
        """;

    private static final String translateToEnglishPromptTemplate = """
        以下を英語に翻訳してください。
        ---
        {query}
        """;

    private static final String translateToJapanesePromptTemplate = """
        以下を日本語に翻訳してください。
        ---
        {query}
        """;

    private static final String routingPromptTemplate = """
        以下のユーザークエリに基づいて、適切なルートを選択してください。
        ---
        {query}
        """;

    public OpenAIChatService(ApplicationContext context, ChatMemory chatMemory, SyncMcpToolCallbackProvider syncMcpToolCallbackProvider, VectorStore vectorStore) {
        this.context = context;
        this.chatMemory = chatMemory;
        this.syncMcpToolCallbackProvider = syncMcpToolCallbackProvider;
        this.vectorStore = vectorStore;
    }

    public String withUserMessage(String userMessage) {
        ChatModel model = context.getBean(ChatModel.class);
        ChatClient client = ChatClient.create(model);
        return client.prompt(userMessage).call().content();
    }

    public AccountTitle classifyExpenses(String expense) {
        ChatModel model = context.getBean(ChatModel.class);
        ChatClient client = ChatClient.create(model);
        return client.prompt()
                .user(promptUserSpec -> promptUserSpec
                                .text(classifyExpensesPromptTemplate)
                                .param("expense", expense)
                        )
                .call().entity(AccountTitle.class);
    }
    public AssistantUITextMessagePart classifyExpenses(Prompt prompt) {
        ChatModel model = context.getBean(ChatModel.class);
        ChatClient client = ChatClient.create(model);
        var accountTitle = client.prompt(prompt)
                .call().entity(AccountTitle.class);
        return new AssistantUITextMessagePart(accountTitle.getEnglishNameAlternative());
    }

    public AssistantUITextMessagePart addPet(Prompt prompt) {
        ChatModel model = context.getBean(ChatModel.class);
        ChatClient client = ChatClient.builder(model)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultToolCallbacks(syncMcpToolCallbackProvider).build();
        return client.prompt(prompt)
                .advisors(advisorSpec ->
                        advisorSpec.param(CONVERSATION_ID, getCurrentUsername() + "-" + getCurrentSessionId())
                )
                .tools(new PetStoreTools())
                .toolContext(Map.of("JSESSIONID", getCurrentSessionId()))
                .call().entity(AssistantUITextMessagePart.class);
    }

    public AssistantUITextMessagePart withPrompt(Prompt prompt) {
        ChatModel model = context.getBean(ChatModel.class);
        ChatClient client = ChatClient.builder(model)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultToolCallbacks(syncMcpToolCallbackProvider).build();
        return client.prompt(prompt)
                .advisors(advisorSpec ->
                    advisorSpec.param(CONVERSATION_ID, getCurrentUsername() + "-" + getCurrentSessionId())
                )
                .tools(new PetStoreTools(), new RagSearchTool(vectorStore))
                .toolContext(Map.of("JSESSIONID", getCurrentSessionId(), "username", getCurrentUsername()))
                .call().entity(AssistantUITextMessagePart.class);
    }

    public AssistantUITextMessagePart searchInEnglish(Prompt prompt) {
        ChatModel model = context.getBean(ChatModel.class);
        ChatClient client = ChatClient.builder(model)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();

        var englishPrompt = client.prompt()
                .user(promptUserSpec -> promptUserSpec
                        .text(translateToEnglishPromptTemplate)
                        .param("query", prompt.getContents())
                ).call().entity(TranslationResult.class).getTranslatedText();
        logger.info("English Prompt: {}", englishPrompt);
        var englishSearchResult = client.prompt()
                .user(promptUserSpec -> promptUserSpec
                        .text(englishPrompt))
                .toolCallbacks(syncMcpToolCallbackProvider)
                .call().content();
        logger.info("English Search Result: {}", englishSearchResult);
        return client.prompt()
                .user(promptUserSpec -> promptUserSpec
                        .text(translateToJapanesePromptTemplate)
                        .param("query", englishSearchResult))
                .call().entity(AssistantUITextMessagePart.class);
    }

    public AssistantUITextMessagePart withRouting(Prompt prompt) {
        ChatModel model = context.getBean(ChatModel.class);
        ChatClient client = ChatClient.builder(model)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
        var workflowRoute = client.prompt()
                .user(promptUserSpec -> promptUserSpec
                        .text(translateToEnglishPromptTemplate)
                        .param("query", prompt.getContents())
                ).call().entity(WorkflowRoute.class);
        logger.info("workflowRoute: {}", workflowRoute);
        return switch (workflowRoute) {
            case EXPENSE_CLASSIFICATION -> classifyExpenses(prompt);
            case SEARCH_IN_ENGLISH -> searchInEnglish(prompt);
            case PET_STORE_ADD_PET -> addPet(prompt);
        };
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else if (principal instanceof String username) {
            return username;
        } else {
            return authentication.getName();
        }
    }

    private String getCurrentSessionId() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return "Context outside HTTP Request";
        }

        HttpServletRequest request = attributes.getRequest();
        HttpSession session = request.getSession(false);
        if (session != null) {
            return session.getId();
        } else {
            return "No active session in service layer";
        }
    }

    @Setter
    @Getter
    public static class TranslationResult {
        private String translatedText;
    }

    public enum WorkflowRoute {
        EXPENSE_CLASSIFICATION,
        SEARCH_IN_ENGLISH,
        PET_STORE_ADD_PET
    }
}