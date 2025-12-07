package com.example.spring_ai_demo.adapter.out.saas;

import com.example.spring_ai_demo.adapter.in.web.dto.AssistantUICompleteAttachment;
import com.example.spring_ai_demo.adapter.in.web.dto.AssistantUITextMessagePart;
import com.example.spring_ai_demo.adapter.out.persistence.AttachmentStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FileTranslator {
    private final AttachmentStore attachmentStore;

    private static final Logger logger = LoggerFactory.getLogger(FileTranslator.class);

    public FileTranslator(AttachmentStore attachmentStore) {
        this.attachmentStore = attachmentStore;
    }

    @Tool(name = "translate_file", description = "Translates a file using Translate API.")
    public String translate(@ToolParam(description = "fileId") String fileId,
                            @ToolParam(description = "Language code of the source language in ISO 639-1 format") String sourceLanguageCode,
                            @ToolParam(description = "Language code of the target language in ISO 639-1 format") String targetLanguageCode) {
        logger.info("Translating file {} from {} to {}", fileId, sourceLanguageCode, targetLanguageCode);

        var attachment = attachmentStore.getAttachment(fileId);
        String filename = attachment.getName();
        
        attachmentStore.addAttachment(fileId + "_result", new AssistantUICompleteAttachment(
                fileId + "_result",
                "document",
                getBaseName(filename) + "_translated." + getExtension(filename),
                "text/plain",
                List.of(new AssistantUITextMessagePart("""
                        It's sunny today.
                        Tomorrow will be cloudy.
                        """))
        ));

        return filename;
    }

    private static String getBaseName(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');

        // ドットが存在し、かつファイル名が空でない場合
        if (lastDotIndex > 0) {
            // 最後のドットの手前までを切り出す
            return filename.substring(0, lastDotIndex);
        }

        // ドットがない、またはドットで始まる（.bashrcなど）場合は、元の文字列を返す
        return filename;
    }

    private static String getExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');

        // ドットが存在し、かつドットが文字列の最後ではない場合
        if (lastDotIndex != -1 && lastDotIndex < filename.length() - 1) {
            // 最後のドットの次の文字（lastDotIndex + 1）から最後までを切り出す
            return filename.substring(lastDotIndex + 1);
        }

        // ドットがない、またはドットが最後の文字である（例: "file."）場合は空文字列を返す
        return "";
    }
}
