package com.fiap.biblioteca.dto.livro;

import com.fiap.biblioteca.domain.Livro;

import java.time.Instant;

public record LivroResponse(
        Long id,
        String titulo,
        String autor,
        String isbn,
        String editora,
        Integer anoPublicacao,
        String categoria,
        int quantidadeTotal,
        int quantidadeDisponivel,
        boolean disponivel,
        Instant createdAt,
        Instant updatedAt
) {
    public static LivroResponse from(Livro livro) {
        return new LivroResponse(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getIsbn(),
                livro.getEditora(),
                livro.getAnoPublicacao(),
                livro.getCategoria(),
                livro.getQuantidadeTotal(),
                livro.getQuantidadeDisponivel(),
                livro.isDisponivel(),
                livro.getCreatedAt(),
                livro.getUpdatedAt()
        );
    }
}
