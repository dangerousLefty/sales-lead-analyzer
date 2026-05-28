package com.hamza.smartleadgenerator.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.service.registry.ImportHttpServices;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@ImportHttpServices(group = "huggingFace", types = HuggingFaceService.class)
public class HttpClientConfig {

    @Value("${ai.huggingface.token}")
    private String huggingFaceToken;

    @Bean
    RestClientHttpServiceGroupConfigurer groupConfigurer(){
        return groups -> {
            groups
                    .filterByName("huggingFace")
                    .forEachClient((group, builder) -> {
                        builder.baseUrl("https://router.huggingface.co/v1")
                                .defaultHeader("Authorization", "Bearer " + huggingFaceToken)
                                .build();
                    });
        };
    }
}
