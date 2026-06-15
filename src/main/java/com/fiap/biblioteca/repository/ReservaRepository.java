package com.fiap.biblioteca.repository;

import com.fiap.biblioteca.domain.Reserva;
import com.fiap.biblioteca.domain.enums.StatusReserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    Page<Reserva> findByUsuarioId(Long usuarioId, Pageable pageable);

    boolean existsByLivroIdAndUsuarioIdAndStatus(Long livroId, Long usuarioId, StatusReserva status);

    // primeiro da fila (mais antigo) - usado quando devolvem o livro
    Optional<Reserva> findFirstByLivroIdAndStatusOrderByDataReservaAsc(Long livroId, StatusReserva status);
}
