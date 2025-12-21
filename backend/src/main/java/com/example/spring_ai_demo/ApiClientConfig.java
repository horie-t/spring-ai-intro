package com.example.spring_ai_demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import com.example.petstore.client.invoker.ApiClient;
import com.example.petstore.client.api.PetApi;

@Configuration
public class ApiClientConfig {

    @Bean
    public ApiClient petStoreApiClient(RestClient.Builder builder) {
        RestClient restClient = builder.build();
        return new ApiClient(restClient);
    }

    @Bean
    public PetApi petApi(ApiClient apiClient) {
        return new PetApi(apiClient);
    }
}