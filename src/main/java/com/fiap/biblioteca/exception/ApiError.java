package com.fiap.biblioteca.exception;

import java.time.Instant;
import java.util.List;

// corpo padrao de erro que volta pro cliente
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<CampoInvalido> fieldErrors
) {
    public record CampoInvalido(String campo, String mensagem) {
    }

    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(Instant.now(), status, error, message, path, List.of());
    }

    public static ApiError of(int status, String error, String message, String path,
                              List<CampoInvalido> fieldErrors) {
        return new ApiError(Instant.now(), status, error, message, path, fieldErrors);
    }
}
