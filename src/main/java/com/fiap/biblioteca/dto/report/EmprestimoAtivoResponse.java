package com.fiap.biblioteca.dto.report;

import com.fiap.biblioteca.domain.Emprestimo;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

// diasParaDevolucao fica negativo quando o emprestimo ja atrasou
public record EmprestimoAtivoResponse(
        Long emprestimoId,
        Long livroId,
        String titulo,
        String isbn,
        Long usuarioId,
        String usuarioNome,
        LocalDate dataPrevistaDevolucao,
        long diasParaDevolucao,
        boolean atrasado
) {
    public static EmprestimoAtivoResponse from(Emprestimo e, LocalDate referencia) {
        long dias = ChronoUnit.DAYS.between(referencia, e.getDataPrevistaDevolucao());
        return new EmprestimoAtivoResponse(
                e.getId(),
                e.getLivro().getId(),
                e.getLivro().getTitulo(),
                e.getLivro().getIsbn(),
                e.getUsuario().getId(),
                e.getUsuario().getNome(),
                e.getDataPrevistaDevolucao(),
                dias,
                e.isAtrasado(referencia)
        );
    }

    public static EmprestimoAtivoResponse from(Emprestimo e) {
        return from(e, LocalDate.now(ZoneOffset.UTC));
    }
}
