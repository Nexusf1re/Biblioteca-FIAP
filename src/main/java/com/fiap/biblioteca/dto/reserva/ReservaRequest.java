package com.fiap.biblioteca.dto.reserva;

import jakarta.validation.constraints.NotNull;

public record ReservaRequest(

        @NotNull(message = "O id do livro e obrigatorio")
        Long livroId,

        @NotNull(message = "O id do usuario e obrigatorio")
        Long usuarioId
) {
}
