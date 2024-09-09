package org.sergp.paymentservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;

import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Регистрируем модуль для работы с Java Time API
        return objectMapper;
    }

    @Bean//(name = "customerWebClient")
    public WebClient webClient(ObjectMapper objectMapper) {
        return WebClient.builder()
                .codecs(clientCodecConfigurer -> {
                    clientCodecConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
                    clientCodecConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
                }).build();
    }

}
