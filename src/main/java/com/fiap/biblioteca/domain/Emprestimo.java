package com.fiap.biblioteca.domain;

import com.fiap.biblioteca.domain.enums.StatusEmprestimo;
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

@Entity
@Table(name = "emprestimo", indexes = {
        @Index(name = "idx_emp_livro", columnList = "livro_id"),
        @Index(name = "idx_emp_usuario", columnList = "usuario_id"),
        @Index(name = "idx_emp_status", columnList = "status")
})
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // instante exato do emprestimo (UTC). a data prevista/real sao so data, sem hora
    @Column(name = "data_emprestimo", nullable = false)
    private Instant dataEmprestimo;

    @Column(name = "data_prevista_devolucao", nullable = false)
    private LocalDate dataPrevistaDevolucao;

    @Column(name = "data_devolucao_real")
    private LocalDate dataDevolucaoReal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusEmprestimo status;

    public Emprestimo() {
    }

    public Emprestimo(Livro livro, Usuario usuario, Instant dataEmprestimo, LocalDate dataPrevistaDevolucao) {
        this.livro = livro;
        this.usuario = usuario;
        this.dataEmprestimo = dataEmprestimo;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.status = StatusEmprestimo.ATIVO;
    }

    public void registrarDevolucao(LocalDate dataDevolucao) {
        this.dataDevolucaoReal = dataDevolucao;
        this.status = StatusEmprestimo.DEVOLVIDO;
    }

    /**
     * So conta como atrasado se ainda nao foi devolvido e a data de referencia ja passou do prazo.
     */
    public boolean isAtrasado(LocalDate referencia) {
        return dataDevolucaoReal == null && referencia.isAfter(dataPrevistaDevolucao);
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

    public Instant getDataEmprestimo() {
        return dataEmprestimo;
    }

    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public LocalDate getDataDevolucaoReal() {
        return dataDevolucaoReal;
    }

    public StatusEmprestimo getStatus() {
        return status;
    }

    public void setStatus(StatusEmprestimo status) {
        this.status = status;
    }
}
