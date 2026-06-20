package br.com.pelorca.desafio.msdesafio.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class JacksonConfig {

    @Bean
    public JsonMapperBuilderCustomizer snakeCaseJsonMapperBuilderCustomizer() {
        return (JsonMapper.Builder builder) -> builder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }
}
