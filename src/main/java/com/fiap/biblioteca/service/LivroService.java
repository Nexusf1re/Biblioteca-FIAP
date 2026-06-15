package com.fiap.biblioteca.service;

import com.fiap.biblioteca.domain.Livro;
import com.fiap.biblioteca.domain.enums.StatusEmprestimo;
import com.fiap.biblioteca.dto.PageResponse;
import com.fiap.biblioteca.dto.livro.LivroRequest;
import com.fiap.biblioteca.dto.livro.LivroResponse;
import com.fiap.biblioteca.exception.RecursoNaoEncontradoException;
import com.fiap.biblioteca.exception.RegraNegocioException;
import com.fiap.biblioteca.repository.EmprestimoRepository;
import com.fiap.biblioteca.repository.LivroRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LivroService {

    private final LivroRepository livroRepository;
    private final EmprestimoRepository emprestimoRepository;

    public LivroService(LivroRepository livroRepository, EmprestimoRepository emprestimoRepository) {
        this.livroRepository = livroRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    @Transactional
    public LivroResponse criar(LivroRequest req) {
        if (livroRepository.existsByIsbn(req.isbn())) {
            throw new RegraNegocioException("Ja existe um livro cadastrado com o ISBN " + req.isbn());
        }
        Livro livro = new Livro(req.titulo(), req.autor(), req.isbn(), req.editora(),
                req.anoPublicacao(), req.categoria(), req.quantidadeTotal());
        return LivroResponse.from(livroRepository.save(livro));
    }

    // cadastro em lote - usado quando o bibliotecario sobe varios livros de uma vez
    @Transactional
    public List<LivroResponse> criarEmLote(List<LivroRequest> requests) {
        List<String> isbns = requests.stream().map(LivroRequest::isbn).toList();
        if (isbns.stream().distinct().count() != isbns.size()) {
            throw new RegraNegocioException("Ha ISBNs duplicados na requisicao de lote");
        }
        for (String isbn : isbns) {
            if (livroRepository.existsByIsbn(isbn)) {
                throw new RegraNegocioException("Ja existe um livro cadastrado com o ISBN " + isbn);
            }
        }
        List<Livro> livros = requests.stream()
                .map(r -> new Livro(r.titulo(), r.autor(), r.isbn(), r.editora(),
                        r.anoPublicacao(), r.categoria(), r.quantidadeTotal()))
                .toList();
        return livroRepository.saveAll(livros).stream()
                .map(LivroResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<LivroResponse> buscar(String titulo, String autor, String isbn,
                                              boolean somenteDisponiveis, Pageable pageable) {
        return PageResponse.from(
                livroRepository.buscar(emptyToNull(titulo), emptyToNull(autor), emptyToNull(isbn),
                                somenteDisponiveis, pageable)
                        .map(LivroResponse::from));
    }

    @Transactional(readOnly = true)
    public LivroResponse buscarPorId(Long id) {
        return LivroResponse.from(obter(id));
    }

    @Transactional
    public LivroResponse atualizar(Long id, LivroRequest req) {
        Livro livro = obter(id);
        if (livroRepository.existsByIsbnAndIdNot(req.isbn(), id)) {
            throw new RegraNegocioException("Ja existe outro livro cadastrado com o ISBN " + req.isbn());
        }
        // quantos exemplares ja estao na rua. nao da pra deixar o total ficar abaixo disso
        int emprestados = livro.getQuantidadeTotal() - livro.getQuantidadeDisponivel();
        if (req.quantidadeTotal() < emprestados) {
            throw new RegraNegocioException(
                    "A quantidade total (" + req.quantidadeTotal() + ") nao pode ser menor que o numero de exemplares emprestados ("
                            + emprestados + ")");
        }
        livro.setTitulo(req.titulo());
        livro.setAutor(req.autor());
        livro.setIsbn(req.isbn());
        livro.setEditora(req.editora());
        livro.setAnoPublicacao(req.anoPublicacao());
        livro.setCategoria(req.categoria());
        livro.setQuantidadeTotal(req.quantidadeTotal());
        livro.setQuantidadeDisponivel(req.quantidadeTotal() - emprestados);
        return LivroResponse.from(livro);
    }

    @Transactional
    public void excluir(Long id) {
        Livro livro = obter(id);
        // se ainda tem livro emprestado nao deixa apagar, senao perde o vinculo
        if (emprestimoRepository.existsByLivroIdAndStatus(id, StatusEmprestimo.ATIVO)) {
            throw new RegraNegocioException("Nao e possivel excluir um livro que possui emprestimos ativos");
        }
        livroRepository.delete(livro);
    }

    private Livro obter(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Livro", id));
    }

    private static String emptyToNull(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor;
    }
}
