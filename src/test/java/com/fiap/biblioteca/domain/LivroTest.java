package com.fiap.biblioteca.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LivroTest {

    private Livro novoLivro(int total) {
        return new Livro("Titulo", "Autor", "9780000000001", "Editora", 2020, "Categoria", total);
    }

    @Test
    void novoLivroComecaTotalmenteDisponivel() {
        Livro livro = novoLivro(3);
        assertThat(livro.getQuantidadeDisponivel()).isEqualTo(3);
        assertThat(livro.isDisponivel()).isTrue();
    }

    @Test
    void emprestarExemplarDecrementaDisponibilidade() {
        Livro livro = novoLivro(2);
        livro.emprestarExemplar();
        assertThat(livro.getQuantidadeDisponivel()).isEqualTo(1);
    }

    @Test
    void emprestarSemExemplarDisponivelLancaExcecao() {
        Livro livro = novoLivro(1);
        livro.emprestarExemplar();
        assertThat(livro.isDisponivel()).isFalse();
        assertThatThrownBy(livro::emprestarExemplar).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void devolverExemplarNaoUltrapassaOTotal() {
        Livro livro = novoLivro(1);
        livro.devolverExemplar();
        assertThat(livro.getQuantidadeDisponivel()).isEqualTo(1);
    }
}
