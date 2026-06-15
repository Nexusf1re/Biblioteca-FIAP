package com.fiap.biblioteca.dto.report;

// uma linha do relatorio de mais emprestados (preenchido direto pela query)
public record LivroMaisEmprestadoResponse(
        Long livroId,
        String titulo,
        String autor,
        String isbn,
        Long totalEmprestimos
) {
}
