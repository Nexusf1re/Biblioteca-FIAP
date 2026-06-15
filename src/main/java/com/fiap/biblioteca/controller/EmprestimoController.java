package com.fiap.biblioteca.controller;

import com.fiap.biblioteca.domain.enums.StatusEmprestimo;
import com.fiap.biblioteca.dto.PageResponse;
import com.fiap.biblioteca.dto.emprestimo.EmprestimoRequest;
import com.fiap.biblioteca.dto.emprestimo.EmprestimoResponse;
import com.fiap.biblioteca.service.EmprestimoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emprestimos")
@Tag(name = "Emprestimos", description = "Registro de emprestimos e devolucoes de livros")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    public EmprestimoController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    @Operation(summary = "Registra um emprestimo (calcula a previsao de devolucao em UTC)")
    @PostMapping
    public ResponseEntity<EmprestimoResponse> emprestar(@Valid @RequestBody EmprestimoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(emprestimoService.emprestar(req));
    }

    @Operation(summary = "Registra a devolucao de um emprestimo e atende a proxima reserva da fila")
    @PatchMapping("/{id}/devolucao")
    public EmprestimoResponse devolver(@PathVariable Long id) {
        return emprestimoService.devolver(id);
    }

    @Operation(summary = "Lista emprestimos (opcionalmente filtrando por status) com paginacao")
    @GetMapping
    public PageResponse<EmprestimoResponse> listar(
            @RequestParam(required = false) StatusEmprestimo status,
            @ParameterObject @PageableDefault(size = 10, sort = "dataEmprestimo") Pageable pageable) {
        return emprestimoService.listar(status, pageable);
    }

    @Operation(summary = "Consulta um emprestimo pelo id")
    @GetMapping("/{id}")
    public EmprestimoResponse buscarPorId(@PathVariable Long id) {
        return emprestimoService.buscarPorId(id);
    }
}
