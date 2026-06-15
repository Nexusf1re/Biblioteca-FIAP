package com.fiap.biblioteca.dto.usuario;

import com.fiap.biblioteca.domain.Usuario;
import com.fiap.biblioteca.domain.enums.TipoUsuario;

import java.time.Instant;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        TipoUsuario tipo,
        boolean ativo,
        Instant dataCadastro
) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipo(),
                usuario.isAtivo(),
                usuario.getDataCadastro()
        );
    }
}
