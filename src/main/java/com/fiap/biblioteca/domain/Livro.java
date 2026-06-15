package com.fiap.biblioteca.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "livro", indexes = {
        @Index(name = "idx_livro_isbn", columnList = "isbn", unique = true),
        @Index(name = "idx_livro_titulo", columnList = "titulo"),
        @Index(name = "idx_livro_autor", columnList = "autor")
})
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String autor;

    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    private String editora;

    @Column(name = "ano_publicacao")
    private Integer anoPublicacao;

    private String categoria;

    // quantos exemplares existem x quantos estao livres pra emprestar
    @Column(name = "quantidade_total", nullable = false)
    private int quantidadeTotal;

    @Column(name = "quantidade_disponivel", nullable = false)
    private int quantidadeDisponivel;

    @Version
    private Long versao; // evita dois emprestimos pegarem o mesmo ultimo exemplar ao mesmo tempo

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public Livro() {
    }

    public Livro(String titulo, String autor, String isbn, String editora,
                 Integer anoPublicacao, String categoria, int quantidadeTotal) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.editora = editora;
        this.anoPublicacao = anoPublicacao;
        this.categoria = categoria;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeDisponivel = quantidadeTotal;
    }

    public boolean isDisponivel() {
        return quantidadeDisponivel > 0;
    }

    public void emprestarExemplar() {
        if (quantidadeDisponivel <= 0) {
            throw new IllegalStateException("Nao ha exemplares disponiveis do livro: " + titulo);
        }
        quantidadeDisponivel--;
    }

    public void devolverExemplar() {
        // nao pode passar do total (alguem devolvendo a mais por engano)
        if (quantidadeDisponivel < quantidadeTotal) {
            quantidadeDisponivel++;
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getEditora() {
        return editora;
    }

    public void setEditora(String editora) {
        this.editora = editora;
    }

    public Integer getAnoPublicacao() {
        return anoPublicacao;
    }

    public void setAnoPublicacao(Integer anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public void setQuantidadeTotal(int quantidadeTotal) {
        this.quantidadeTotal = quantidadeTotal;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(int quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public Long getVersao() {
        return versao;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
