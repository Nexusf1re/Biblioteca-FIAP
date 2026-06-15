package com.fiap.biblioteca.dto.reserva;

import com.fiap.biblioteca.domain.Reserva;
import com.fiap.biblioteca.domain.enums.StatusReserva;

import java.time.Instant;
import java.time.LocalDate;

public record ReservaResponse(
        Long id,
        Long livroId,
        String livroTitulo,
        Long usuarioId,
        String usuarioNome,
        Instant dataReserva,
        LocalDate dataExpiracao,
        StatusReserva status
) {
    public static ReservaResponse from(Reserva r) {
        return new ReservaResponse(
                r.getId(),
                r.getLivro().getId(),
                r.getLivro().getTitulo(),
                r.getUsuario().getId(),
                r.getUsuario().getNome(),
                r.getDataReserva(),
                r.getDataExpiracao(),
                r.getStatus()
        );
    }
}
