package br.com.pelorca.desafio.msdesafio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI msDesafioOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ms-desafio API")
                        .version("v1")
                        .description("API REST de Clientes - Desafio Final Bootcamp Arquiteto de Software"));
    }
}
