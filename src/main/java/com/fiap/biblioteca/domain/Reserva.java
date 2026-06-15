package com.fiap.biblioteca.domain;

import com.fiap.biblioteca.domain.enums.StatusReserva;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

// Reserva entra quando o livro nao tem exemplar livre. Quando alguem devolve, a fila anda (FIFO).
@Entity
@Table(name = "reserva", indexes = {
        @Index(name = "idx_res_livro", columnList = "livro_id"),
        @Index(name = "idx_res_usuario", columnList = "usuario_id"),
        @Index(name = "idx_res_status", columnList = "status")
})
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "data_reserva", nullable = false)
    private Instant dataReserva;

    @Column(name = "data_expiracao")
    private LocalDate dataExpiracao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusReserva status;

    public Reserva() {
    }

    public Reserva(Livro livro, Usuario usuario, Instant dataReserva) {
        this.livro = livro;
        this.usuario = usuario;
        this.dataReserva = dataReserva;
        this.status = StatusReserva.ATIVA;
    }

    public void atender(LocalDate dataExpiracao) {
        this.status = StatusReserva.ATENDIDA;
        this.dataExpiracao = dataExpiracao;
    }

    public void cancelar() {
        this.status = StatusReserva.CANCELADA;
    }

    public Long getId() {
        return id;
    }

    public Livro getLivro() {
        return livro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Instant getDataReserva() {
        return dataReserva;
    }

    public LocalDate getDataExpiracao() {
        return dataExpiracao;
    }

    public StatusReserva getStatus() {
        return status;
    }

    public void setStatus(StatusReserva status) {
        this.status = status;
    }
}
