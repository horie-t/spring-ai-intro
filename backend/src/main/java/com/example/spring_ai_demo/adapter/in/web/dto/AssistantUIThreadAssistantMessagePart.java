package com.example.spring_ai_demo.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type" // JSONリクエストボディに含まれる型情報のキー
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AssistantUITextMessagePart.class, name = "text"),
        @JsonSubTypes.Type(value = AssistantUIFileMessagePart.class, name = "file")
})
public interface AssistantUIThreadAssistantMessagePart {
}
