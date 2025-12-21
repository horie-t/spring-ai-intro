package com.example.spring_ai_demo.adapter.out.saas;

import com.example.petstore.client.api.PetApi;
import com.example.petstore.client.invoker.ApiClient;
import com.example.petstore.client.model.Pet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.web.client.RestClient;

public class PetStoreTools {
    private static final Logger logger = LoggerFactory.getLogger(PetStoreTools.class);

    @Tool(description = "Add a new pet to the Awesome Pet Store Service")
    public void addPet(@ToolParam(description = "Name of the pet to add") String name, ToolContext context) {
        var sessionId = context.getContext().get("JSESSIONID").toString();
        var petApi = new PetApi(new ApiClient(RestClient.builder().build()));
        petApi.getApiClient()
                .setBasePath("http://localhost:8080/api/pet-store/")
                .addDefaultHeader("Cookie", "JSESSIONID=" + sessionId + ";");

        var result = petApi.addPet(new Pet().name(name));
        logger.info("Pet: {}", result.toString());
    }
}
