package com.fiap.biblioteca.repository;

import com.fiap.biblioteca.domain.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    Optional<Livro> findByIsbn(String isbn);

    // Busca por filtros opcionais. Filtro nulo e ignorado (por isso o "IS NULL OR ...").
    // O CAST(... AS string) e pra nao quebrar no Postgres com parametro nulo (lower(bytea)).
    @Query("""
            SELECT l FROM Livro l
            WHERE (:titulo IS NULL OR LOWER(l.titulo) LIKE LOWER(CONCAT('%', CAST(:titulo AS string), '%')))
              AND (:autor IS NULL OR LOWER(l.autor) LIKE LOWER(CONCAT('%', CAST(:autor AS string), '%')))
              AND (:isbn IS NULL OR l.isbn = CAST(:isbn AS string))
              AND (:somenteDisponiveis = false OR l.quantidadeDisponivel > 0)
            """)
    Page<Livro> buscar(@Param("titulo") String titulo,
                       @Param("autor") String autor,
                       @Param("isbn") String isbn,
                       @Param("somenteDisponiveis") boolean somenteDisponiveis,
                       Pageable pageable);
}
