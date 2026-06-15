package com.fiap.biblioteca.repository;

import com.fiap.biblioteca.dto.PageResponse;
import com.fiap.biblioteca.dto.livro.LivroRequest;
import com.fiap.biblioteca.dto.livro.LivroResponse;
import com.fiap.biblioteca.service.LivroService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

// regressao no Postgres de verdade (Testcontainers).
// a busca com filtro nulo quebrava com "lower(bytea) does not exist" - o H2 nao pega isso.
// se nao tiver Docker na maquina, o teste se desabilita sozinho.
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class LivroBuscaPostgresContainerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        registry.add("spring.h2.console.enabled", () -> "false");
    }

    @Autowired
    private LivroService livroService;

    @Test
    void buscaPorFiltrosFuncionaNoPostgres() {
        LivroResponse criado = livroService.criar(
                new LivroRequest("Clean Code", "Robert C. Martin", "9780132350884",
                        "Prentice Hall", 2008, "Engenharia", 2));

        // Busca sem nenhum filtro (titulo/autor/isbn nulos) — reproduzia lower(bytea) no Postgres.
        PageResponse<LivroResponse> semFiltro =
                livroService.buscar(null, null, null, false, PageRequest.of(0, 10));
        assertThat(semFiltro.totalElements()).isGreaterThanOrEqualTo(1);

        // Busca por titulo (case-insensitive).
        PageResponse<LivroResponse> porTitulo =
                livroService.buscar("clean", null, null, false, PageRequest.of(0, 10));
        assertThat(porTitulo.content())
                .extracting(LivroResponse::id)
                .contains(criado.id());

        // Busca por ISBN exato.
        PageResponse<LivroResponse> porIsbn =
                livroService.buscar(null, null, "9780132350884", false, PageRequest.of(0, 10));
        assertThat(porIsbn.totalElements()).isEqualTo(1);
    }
}
