package com.fiap.biblioteca.repository;

import com.fiap.biblioteca.domain.Emprestimo;
import com.fiap.biblioteca.domain.enums.StatusEmprestimo;
import com.fiap.biblioteca.dto.report.LivroMaisEmprestadoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    Page<Emprestimo> findByUsuarioId(Long usuarioId, Pageable pageable);

    Page<Emprestimo> findByStatus(StatusEmprestimo status, Pageable pageable);

    boolean existsByLivroIdAndStatus(Long livroId, StatusEmprestimo status);

    boolean existsByUsuarioIdAndStatus(Long usuarioId, StatusEmprestimo status);

    // Livros mais emprestados, do mais pro menos. Passar PageRequest.of(0,20) pra pegar o top 20.
    @Query("""
            SELECT new com.fiap.biblioteca.dto.report.LivroMaisEmprestadoResponse(
                l.id, l.titulo, l.autor, l.isbn, COUNT(e))
            FROM Emprestimo e
            JOIN e.livro l
            GROUP BY l.id, l.titulo, l.autor, l.isbn
            ORDER BY COUNT(e) DESC
            """)
    List<LivroMaisEmprestadoResponse> findLivrosMaisEmprestados(Pageable pageable);

    // Emprestimos ainda em aberto. JOIN FETCH pra ja trazer livro+usuario e nao cair em N+1
    @Query("""
            SELECT e FROM Emprestimo e
            JOIN FETCH e.livro
            JOIN FETCH e.usuario
            WHERE e.dataDevolucaoReal IS NULL
            ORDER BY e.dataPrevistaDevolucao ASC
            """)
    List<Emprestimo> findEmprestimosEmAberto();
}
