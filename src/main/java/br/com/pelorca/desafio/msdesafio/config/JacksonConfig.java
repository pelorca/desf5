package br.com.pelorca.desafio.msdesafio.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;

/**
 * Applies snake_case naming to all REST request/response JSON throughout the app,
 * via Boot 4's JsonMapper.Builder customization hook (not a replacement ObjectMapper bean,
 * so other Boot-managed Jackson defaults like ProblemDetail serialization stay intact).
 */
@Configuration
public class JacksonConfig {

    @Bean
    public JsonMapperBuilderCustomizer snakeCaseJsonMapperBuilderCustomizer() {
        return (JsonMapper.Builder builder) -> builder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }
}
