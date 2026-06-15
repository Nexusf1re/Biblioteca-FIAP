package com.fiap.biblioteca.dto.usuario;

import com.fiap.biblioteca.domain.enums.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRequest(

        @NotBlank(message = "O nome e obrigatorio")
        String nome,

        @NotBlank(message = "O e-mail e obrigatorio")
        @Email(message = "E-mail invalido")
        String email,

        @NotNull(message = "O tipo de usuario e obrigatorio (ALUNO, PROFESSOR ou MEMBRO)")
        TipoUsuario tipo
) {
}
