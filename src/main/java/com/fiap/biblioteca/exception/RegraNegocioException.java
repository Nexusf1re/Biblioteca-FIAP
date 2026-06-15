package com.fiap.biblioteca.exception;

// vira 409 (conflito) la no handler
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
