package com.viddefe.viddefe_api.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuración global de Jackson para Spring MVC.
 *
 * ObjectMapper limpio sin activateDefaultTyping.
 * Solo maneja soporte de tipos Java Time y fechas.
 *
 * Nota: Redis usa su propio ObjectMapper con polymorphic typing,
 * configurado en RedisConfig, inyectado en RedisTemplate.
 */
@Configuration
public class JacksonConfig {

    /**
     * ObjectMapper principal para REST API / Spring MVC.
     * Sin activateDefaultTyping.
     * Sin @class en JSON.
     */
    @Primary
    @Bean("restObjectMapper")
    public ObjectMapper restObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }
}

