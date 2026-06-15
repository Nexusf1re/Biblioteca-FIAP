package com.fiap.biblioteca.domain.enums;

public enum StatusEmprestimo {
    ATIVO,
    DEVOLVIDO,
    // atraso na pratica e calculado em cima da data prevista, mas deixei o status aqui tambem
    ATRASADO
}
