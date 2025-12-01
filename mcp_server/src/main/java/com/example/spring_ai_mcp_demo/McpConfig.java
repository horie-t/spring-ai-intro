package com.example.spring_ai_mcp_demo;

import com.example.spring_ai_mcp_demo.adapter.out.saas.OpenWeatherMapService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Bean
    ToolCallbackProvider toolCallbackProvider(OpenWeatherMapService openWeatherMapService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(openWeatherMapService)
                .build();
    }
}
