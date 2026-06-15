package com.fiap.biblioteca.controller;

import com.fiap.biblioteca.dto.PageResponse;
import com.fiap.biblioteca.dto.livro.LivroRequest;
import com.fiap.biblioteca.dto.livro.LivroResponse;
import com.fiap.biblioteca.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/livros")
@Tag(name = "Livros", description = "Cadastro, consulta, alteracao e exclusao de livros do acervo")
public class LivroController {

    private final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    @Operation(summary = "Cadastra um novo livro")
    @PostMapping
    public ResponseEntity<LivroResponse> criar(@Valid @RequestBody LivroRequest req,
                                               UriComponentsBuilder uriBuilder) {
        LivroResponse criado = livroService.criar(req);
        URI uri = uriBuilder.path("/api/livros/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @Operation(summary = "Cadastra varios livros em lote (operacao em lote)")
    @PostMapping("/lote")
    @ResponseStatus(HttpStatus.CREATED)
    public List<LivroResponse> criarEmLote(@Valid @RequestBody List<@Valid LivroRequest> requests) {
        return livroService.criarEmLote(requests);
    }

    @Operation(summary = "Busca livros por filtros (titulo, autor, ISBN) com paginacao")
    @GetMapping
    public PageResponse<LivroResponse> buscar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false, defaultValue = "false") boolean somenteDisponiveis,
            @ParameterObject @PageableDefault(size = 10, sort = "titulo", direction = Sort.Direction.ASC) Pageable pageable) {
        return livroService.buscar(titulo, autor, isbn, somenteDisponiveis, pageable);
    }

    @Operation(summary = "Consulta um livro pelo id")
    @GetMapping("/{id}")
    public LivroResponse buscarPorId(@PathVariable Long id) {
        return livroService.buscarPorId(id);
    }

    @Operation(summary = "Altera um livro existente")
    @PutMapping("/{id}")
    public LivroResponse atualizar(@PathVariable Long id, @Valid @RequestBody LivroRequest req) {
        return livroService.atualizar(id, req);
    }

    @Operation(summary = "Exclui um livro (bloqueado se houver emprestimos ativos)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        livroService.excluir(id);
    }
}
