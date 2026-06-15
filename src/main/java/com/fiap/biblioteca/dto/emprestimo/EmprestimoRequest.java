package com.fiap.biblioteca.dto.emprestimo;

import jakarta.validation.constraints.NotNull;

public record EmprestimoRequest(

        @NotNull(message = "O id do livro e obrigatorio")
        Long livroId,

        @NotNull(message = "O id do usuario e obrigatorio")
        Long usuarioId
) {
}
