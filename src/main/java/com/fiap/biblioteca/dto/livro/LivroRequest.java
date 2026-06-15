package com.fiap.biblioteca.dto.livro;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LivroRequest(

        @NotBlank(message = "O titulo e obrigatorio")
        String titulo,

        @NotBlank(message = "O autor e obrigatorio")
        String autor,

        @NotBlank(message = "O ISBN e obrigatorio")
        @Size(min = 10, max = 20, message = "O ISBN deve ter entre 10 e 20 caracteres")
        String isbn,

        String editora,

        Integer anoPublicacao,

        String categoria,

        @NotNull(message = "A quantidade total e obrigatoria")
        @Min(value = 1, message = "A quantidade total deve ser ao menos 1")
        Integer quantidadeTotal
) {
}
