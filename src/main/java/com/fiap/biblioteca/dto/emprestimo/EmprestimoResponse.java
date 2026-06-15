package com.fiap.biblioteca.dto.emprestimo;

import com.fiap.biblioteca.domain.Emprestimo;
import com.fiap.biblioteca.domain.enums.StatusEmprestimo;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public record EmprestimoResponse(
        Long id,
        Long livroId,
        String livroTitulo,
        String livroIsbn,
        Long usuarioId,
        String usuarioNome,
        Instant dataEmprestimo,
        LocalDate dataPrevistaDevolucao,
        LocalDate dataDevolucaoReal,
        StatusEmprestimo status,
        boolean atrasado
) {
    public static EmprestimoResponse from(Emprestimo e) {
        LocalDate hoje = LocalDate.now(ZoneOffset.UTC);
        return new EmprestimoResponse(
                e.getId(),
                e.getLivro().getId(),
                e.getLivro().getTitulo(),
                e.getLivro().getIsbn(),
                e.getUsuario().getId(),
                e.getUsuario().getNome(),
                e.getDataEmprestimo(),
                e.getDataPrevistaDevolucao(),
                e.getDataDevolucaoReal(),
                e.getStatus(),
                e.isAtrasado(hoje)
        );
    }
}
