package com.fiap.biblioteca.exception;

// vira 404 la no handler
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public static RecursoNaoEncontradoException de(String entidade, Long id) {
        return new RecursoNaoEncontradoException(entidade + " nao encontrado(a) para o id " + id);
    }
}
