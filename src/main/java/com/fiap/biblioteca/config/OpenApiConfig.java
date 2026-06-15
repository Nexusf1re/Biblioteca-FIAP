package com.fiap.biblioteca.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// config do Swagger (UI em /swagger-ui.html)
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bibliotecaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API - Sistema de Biblioteca Online")
                        .description("""
                                API REST do Tech Challenge (FIAP - Fase 2) para gestao de uma biblioteca online:
                                cadastro de livros e usuarios, emprestimos, devolucoes, reservas e relatorios.

                                Fuso horario: todas as datas/horas sao tratadas e retornadas em UTC (GMT+0),
                                no formato ISO-8601.""")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Tech Challenge - Biblioteca Online")
                                .email("contato@fiap-biblioteca.dev"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
