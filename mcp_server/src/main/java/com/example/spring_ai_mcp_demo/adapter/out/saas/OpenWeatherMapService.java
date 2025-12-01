package com.example.spring_ai_mcp_demo.adapter.out.saas;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OpenWeatherMapService {

    @Value("${app.openweathermap.api-key}")
    private String apiKey;

    @Tool(name = "fetch current weather for a city",
          description = "Fetches the current weather conditions for a specified city using OpenWeatherMap API.")
    public String fetchCurrentWeather(@ToolParam(description = "City name for which to fetch weather") String city) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .build();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("weather")
                        .queryParam("q", city)
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric")
                        .build())
                .retrieve()
                .body(String.class);
    }
}
