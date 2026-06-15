package com.fiap.biblioteca.controller;

import com.fiap.biblioteca.dto.report.EmprestimoAtivoResponse;
import com.fiap.biblioteca.dto.report.LivroMaisEmprestadoResponse;
import com.fiap.biblioteca.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
@Tag(name = "Relatorios", description = "Relatorios de uso da biblioteca")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @Operation(summary = "Lista dos 20 livros mais emprestados da biblioteca")
    @GetMapping("/livros-mais-emprestados")
    public List<LivroMaisEmprestadoResponse> livrosMaisEmprestados() {
        return relatorioService.livrosMaisEmprestados();
    }

    @Operation(summary = "Livros emprestados no momento e a previsao de devolucao")
    @GetMapping("/emprestimos-ativos")
    public List<EmprestimoAtivoResponse> emprestimosAtivos() {
        return relatorioService.emprestimosAtivos();
    }
}
