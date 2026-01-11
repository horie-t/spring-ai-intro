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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

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

    private static final String threatDetectionSocialEngineeringExpertPromptTemplate = """
        以下のメールのメール送信者の「意図」と「心理的操作」に注目して分析してください。
        緊急性の強調、権威への訴え、罪悪感の利用など、ソーシャルエンジニアリングの手口がないか？
        危険度スコア（0-100）を出力してください。
        ---
        {emailContent}
        """;

    private static final String threatDetectionLinguisticForensicsPromptTemplate = """
        以下のメールのメールの「文体」と「言語パターン」を分析してください。
        不自然な文法、ネイティブらしくない表現の混在、組織の公式文書とかけ離れたフォーマットなどを検知してください。
        危険度スコア（0-100）を出力してください。
        ---
        {emailContent}
        """;

    private static final String threatDetectionTechnicalHeaderAnalystPromptTemplate = """
        以下のメールのメールに含まれるドメイン、URL構造、および技術的な矛盾点（表示名とアドレスの不一致など）に注目してください。
        危険度スコア（0-100）を出力してください。
        ---
        {emailContent}
        """;

    private static final List<String> threatDetectionPromptTemplates = List.of(
            threatDetectionSocialEngineeringExpertPromptTemplate,
            threatDetectionLinguisticForensicsPromptTemplate,
            threatDetectionTechnicalHeaderAnalystPromptTemplate
    );

    private static final String comprehensiveReportPlanningSystemPrompt = """
        包括的なレポートを作成するタスクです。まず必要なセクション（章）を定義し、各セクションについてリサーチ担当者に向けて具体的な指示を文章で作成してください。なお、すべてのセクション案と指示が揃うまでは最終出力を行わず、必要であればステップごとに情報を整理し、内容が十分かつ体系的になるよう思考してください（チェーン・オブ・シンキングを推奨します）。
        
        ## 指示手順
        
        1. テーマ [に関するレポートテーマを入力/指定] について、論点や分析視点を検討し、網羅的に必要な主要セクション（3章以上が望ましい）を箇条書きで洗い出してください。
        2. 各セクション（章）ごとに、その内容を執筆・調査すべき担当者向けの具体的な指示文を作成してください。
           - 指示文には：目的、調査や分析すべき観点、使用するべきデータ・事例・参考資料例、注意事項、論述の深さや分量イメージなどを必ず含めてください。
        3. 各セクション案・指示は一つずつ問題がないか検証し、必要があれば追加・統合・修正・並び順の再考を経て、レポート構成として最適化してください。
        4. まとめのセクションは、セクションの完成後に改めて追加するので、ここでは含めないでください。
        5. 最後に本レポート全体のタイトルを定義してください。
        """;

    private static final String comprehensiveReportSectionCreationPromptTemplate = """
        包括的なレポートの一つの章を執筆するタスクです。章の見出しレベルは2から始めてレポートします。
        ---
        章番号: {sectionNumber}
        章の見出し: {sectionTitle}
        症の内容: {sectionInstructions}
        """;

    private static final String comprehensiveReportSummaryPromptTemplate = """
        包括的なレポートのまとめのセクションを作成します。
        ---------------
        {report}
        """;

    private static final String criticizingTranslationSystemPrompt = """
        文芸翻訳の批評家として、日本語から他言語（または他言語から日本語）への文芸作品の翻訳を批評してください。
        
        - 作品の原文と翻訳文を比較し、訳文の自然さ、表現の正確さ、文化的ニュアンス、スタイル・文体の再現性、読者に与える印象などの観点から詳細に分析します。
        - 必ず批評の根拠となる具体的な原文フレーズや翻訳部分を明示し、その上で理由を挙げて説明してください。
        - 批評箇所ごとに「理由・根拠（比較・観察・分析）」を述べてください。
        - 批評の結論や総評は不要です。
        - 全体としての翻訳スコア（0-100）を出力してください。
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

    public AssistantUITextMessagePart detectSpearPhishing(Prompt prompt) {
        try (ExecutorService executor = Executors.newFixedThreadPool(threatDetectionPromptTemplates.size())) {
            List<CompletableFuture<RiskScore>> riskFutures = threatDetectionPromptTemplates.stream()
                    .map(template ->
                            CompletableFuture.supplyAsync(() -> {
                                ChatModel model = context.getBean(ChatModel.class);
                                ChatClient client = ChatClient.create(model);
                                return client.prompt()
                                        .user(promptUserSpec -> promptUserSpec
                                                .text(template)
                                                .param("emailContent", prompt.getContents())
                                        ).call().entity(RiskScore.class);
                            }, executor)
                    ).toList();
            CompletableFuture.allOf(riskFutures.toArray(new CompletableFuture[0])).join();
            var riskCount = riskFutures.stream()
                    .filter(future -> {
                        try {
                            return future.get().score() >= 80.0;
                        } catch (Exception e) {
                            logger.error("Error getting risk score", e);
                            return false;
                        }
                    })
                    .count();
            if (riskCount >= 2) {
                return new AssistantUITextMessagePart("Spear phishing detected with high risk score.");
            } else {
                return new AssistantUITextMessagePart("No spear phishing detected.");
            }
        }
    }

    public AssistantUITextMessagePart generateComprehensiveReport(Prompt prompt) {
        ChatModel model = context.getBean(ChatModel.class);
        ChatClient client = ChatClient.builder(model)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();

        var plan = client.prompt()
                .system(comprehensiveReportPlanningSystemPrompt)
                .user(promptUserSpec -> promptUserSpec
                        .text(prompt.getContents())
                ).call()
                .entity(ReportPlan.class);

        try (ExecutorService executor = Executors.newFixedThreadPool(plan.reportSections().size())) {
            List<CompletableFuture<ReportSection>> sectionFuture = IntStream.range(0, plan.reportSections().size())
                    .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                        var sectionPlan = plan.reportSections().get(i);
                        ChatModel sectionModel = context.getBean(ChatModel.class);
                        ChatClient sectionClient = ChatClient.create(sectionModel);
                        var sectionContent = sectionClient.prompt()
                                .user(promptUserSpec -> promptUserSpec
                                        .text(comprehensiveReportSectionCreationPromptTemplate)
                                        .param("sectionNumber", String.valueOf(i + 1))
                                        .param("sectionTitle", sectionPlan.sectionName())
                                        .param("sectionInstructions", sectionPlan.sectionInstructions())
                                ).call().content();
                        return new ReportSection(sectionPlan.sectionName(), sectionContent);
                    }, executor))
                    .toList();
            CompletableFuture.allOf(sectionFuture.toArray(new CompletableFuture[0])).join();

            var report = new StringBuffer();
            report.append("# ").append(plan.reportTitle()).append("\n\n");
            for (var section : sectionFuture) {
                report.append(section.join().sectionContent()).append("\n\n");
            }

            var summary = client.prompt()
                    .user(promptUserSpec -> promptUserSpec
                            .text(comprehensiveReportSummaryPromptTemplate)
                            .param("report", report.toString())
                    ).call().content();
            report.append(summary);

            return new AssistantUITextMessagePart(report.toString());
        }
    }

    public AssistantUITextMessagePart translateWithRevision(Prompt prompt) {
        int attempts = 0;
        var translationRecord = translate(prompt);

        logger.info("first translation: {}", translationRecord.translatedText());
        while (true) {
            var evaluation = criticizeTranslation(translationRecord);
            logger.info("evaluation: {}", evaluation);

            if (evaluation.score() >= 80.0 || attempts++ >= 3) {
                return new AssistantUITextMessagePart(translationRecord.translatedText());
            }

            var revisedTranslation = reviseTranslation(translationRecord, evaluation);
            logger.info("revised translation: {}", revisedTranslation.translatedText());
            translationRecord = new TranslationRecord(translationRecord.sourceLanguageCode, translationRecord.targetLanguageCode,
                    translationRecord.sourceText(), revisedTranslation.translatedText());
        }
    }

    private TranslationRecord translate(Prompt prompt) {
        ChatModel model = context.getBean(ChatModel.class);
        ChatClient client = ChatClient.create(model);
        return client.prompt()
                .user(promptUserSpec -> promptUserSpec
                        .text(translateToEnglishPromptTemplate)
                        .param("query", prompt.getContents())
                ).call().entity(TranslationRecord.class);
    }

    private TranslationCriticismEvaluation criticizeTranslation(TranslationRecord translationRecord) {
        ChatModel model = context.getBean(ChatModel.class);
        ChatClient client = ChatClient.create(model);
        return client.prompt()
                .system(criticizingTranslationSystemPrompt)
                .user(promptUserSpec -> promptUserSpec
                        .text("原文: " + translationRecord.sourceText() + "\n\n翻訳文: " + translationRecord.translatedText())
                ).call().entity(TranslationCriticismEvaluation.class);
    }

    private TranslationRecord reviseTranslation(TranslationRecord translationRecord, TranslationCriticismEvaluation evaluation) {
        ChatModel model = context.getBean(ChatModel.class);
        ChatClient client = ChatClient.create(model);

        var revisePromptBuilder = new StringBuilder();
        revisePromptBuilder.append("以下の翻訳文を改訂してください。\n");
        revisePromptBuilder.append("原文: ").append(translationRecord.sourceText()).append("\n");
        revisePromptBuilder.append("翻訳文: ").append(translationRecord.translatedText()).append("\n\n");
        revisePromptBuilder.append("批評:\n");
        for (var criticism : evaluation.criticisms()) {
            revisePromptBuilder.append("- 原文フレーズ: ").append(criticism.sourceSentence()).append("\n");
            revisePromptBuilder.append("  翻訳文フレーズ: ").append(criticism.targetSentence()).append("\n");
            revisePromptBuilder.append("  批評: ").append(criticism.criticism()).append("\n");
            revisePromptBuilder.append("  理由: ").append(criticism.reason()).append("\n\n");
        }

        return client.prompt(revisePromptBuilder.toString()).call().entity(TranslationRecord.class);
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

    public record RiskScore(double score) {
    }

    public record ReportPlan(String reportTitle, List<ReportSectionPlan> reportSections) {
    }

    public record ReportSectionPlan(String sectionName, String sectionInstructions) {
    }

    public record ReportSection(String sectionName, String sectionContent) {
    }

    public record TranslationRecord(String sourceLanguageCode, String targetLanguageCode, String sourceText, String translatedText) {
    }

    public record TranslationCriticism(String sourceSentence, String targetSentence, String criticism, String reason) {
    }

    public record TranslationCriticismEvaluation(List<TranslationCriticism> criticisms, double score) {
    }
}