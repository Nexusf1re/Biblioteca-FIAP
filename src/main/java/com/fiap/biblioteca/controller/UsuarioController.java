package com.fiap.biblioteca.controller;

import com.fiap.biblioteca.dto.PageResponse;
import com.fiap.biblioteca.dto.emprestimo.EmprestimoResponse;
import com.fiap.biblioteca.dto.usuario.UsuarioRequest;
import com.fiap.biblioteca.dto.usuario.UsuarioResponse;
import com.fiap.biblioteca.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestao de usuarios e consulta do historico de emprestimos")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Cadastra um novo usuario")
    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest req,
                                                 UriComponentsBuilder uriBuilder) {
        UsuarioResponse criado = usuarioService.criar(req);
        URI uri = uriBuilder.path("/api/usuarios/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @Operation(summary = "Lista usuarios com paginacao")
    @GetMapping
    public PageResponse<UsuarioResponse> listar(
            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return usuarioService.listar(pageable);
    }

    @Operation(summary = "Consulta um usuario pelo id")
    @GetMapping("/{id}")
    public UsuarioResponse buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    @Operation(summary = "Altera um usuario existente")
    @PutMapping("/{id}")
    public UsuarioResponse atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest req) {
        return usuarioService.atualizar(id, req);
    }

    @Operation(summary = "Exclui um usuario (bloqueado se houver emprestimos ativos)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
    }

    @Operation(summary = "Historico de emprestimos do usuario (paginado)")
    @GetMapping("/{id}/emprestimos")
    public PageResponse<EmprestimoResponse> historico(
            @PathVariable Long id,
            @ParameterObject @PageableDefault(size = 10, sort = "dataEmprestimo") Pageable pageable) {
        return usuarioService.historicoEmprestimos(id, pageable);
    }
}
