package org.homanhquan.productservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    /**
     * Configures a global ObjectMapper bean for JSON serialization/deserialization.
     * ==================================================
     * Method explanation:
     * - JavaTimeModule: Adds support for Java 8 date/time types (e.g. LocalDate, LocalDateTime).
     * - WRITE_DATES_AS_TIMESTAMPS disabled: Serializes dates as ISO-8601 strings (e.g. "2024-01-15T10:30:00")
     *   instead of numeric timestamps (e.g. 1705312200000).
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}