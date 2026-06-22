package com.fiap.biblioteca.dto.usuario;

import com.fiap.biblioteca.domain.enums.TipoUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRequest(

        @Schema(example = "Maria Silva")
        @NotBlank(message = "O nome e obrigatorio")
        String nome,

        @Schema(example = "maria.silva@fiap.com.br")
        @NotBlank(message = "O e-mail e obrigatorio")
        @Email(message = "E-mail invalido")
        String email,

        @Schema(example = "ALUNO", description = "ALUNO, PROFESSOR ou MEMBRO")
        @NotNull(message = "O tipo de usuario e obrigatorio (ALUNO, PROFESSOR ou MEMBRO)")
        TipoUsuario tipo,

        @Schema(example = "true",
                description = "Indica se o usuario esta ativo. Envie false no PUT para inativar "
                        + "(usuario inativo nao pode emprestar nem reservar). "
                        + "Opcional: se omitido, o cadastro assume ativo e a alteracao mantem o valor atual.")
        Boolean ativo
) {

    // Construtor de conveniencia (sem 'ativo'): cadastro assume ativo e alteracao mantem o valor atual.
    public UsuarioRequest(String nome, String email, TipoUsuario tipo) {
        this(nome, email, tipo, null);
    }
}
