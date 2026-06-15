package com.fiap.biblioteca.controller;

import com.fiap.biblioteca.dto.PageResponse;
import com.fiap.biblioteca.dto.reserva.ReservaRequest;
import com.fiap.biblioteca.dto.reserva.ReservaResponse;
import com.fiap.biblioteca.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Reserva de livros indisponiveis (fila FIFO por livro)")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @Operation(summary = "Reserva um livro sem exemplares disponiveis")
    @PostMapping
    public ResponseEntity<ReservaResponse> reservar(@Valid @RequestBody ReservaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.reservar(req));
    }

    @Operation(summary = "Cancela uma reserva")
    @DeleteMapping("/{id}")
    public ReservaResponse cancelar(@PathVariable Long id) {
        return reservaService.cancelar(id);
    }

    @Operation(summary = "Lista reservas (opcionalmente de um usuario) com paginacao")
    @GetMapping
    public PageResponse<ReservaResponse> listar(
            @RequestParam(required = false) Long usuarioId,
            @ParameterObject @PageableDefault(size = 10, sort = "dataReserva") Pageable pageable) {
        return (usuarioId == null)
                ? reservaService.listar(pageable)
                : reservaService.listarPorUsuario(usuarioId, pageable);
    }
}
