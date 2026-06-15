package com.fiap.biblioteca.service;

import com.fiap.biblioteca.domain.Livro;
import com.fiap.biblioteca.domain.Reserva;
import com.fiap.biblioteca.domain.Usuario;
import com.fiap.biblioteca.domain.enums.StatusReserva;
import com.fiap.biblioteca.dto.PageResponse;
import com.fiap.biblioteca.dto.reserva.ReservaRequest;
import com.fiap.biblioteca.dto.reserva.ReservaResponse;
import com.fiap.biblioteca.exception.RecursoNaoEncontradoException;
import com.fiap.biblioteca.exception.RegraNegocioException;
import com.fiap.biblioteca.repository.LivroRepository;
import com.fiap.biblioteca.repository.ReservaRepository;
import com.fiap.biblioteca.repository.UsuarioRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;

    public ReservaService(ReservaRepository reservaRepository,
                          LivroRepository livroRepository,
                          UsuarioRepository usuarioRepository) {
        this.reservaRepository = reservaRepository;
        this.livroRepository = livroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ReservaResponse reservar(ReservaRequest req) {
        Livro livro = livroRepository.findById(req.livroId())
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Livro", req.livroId()));
        Usuario usuario = usuarioRepository.findById(req.usuarioId())
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Usuario", req.usuarioId()));

        if (!usuario.isAtivo()) {
            throw new RegraNegocioException("Usuario inativo nao pode realizar reservas");
        }
        // so faz sentido reservar se nao tem exemplar livre. tendo, e so emprestar
        if (livro.isDisponivel()) {
            throw new RegraNegocioException(
                    "O livro possui exemplares disponiveis; realize um emprestimo em vez de uma reserva");
        }
        if (reservaRepository.existsByLivroIdAndUsuarioIdAndStatus(
                livro.getId(), usuario.getId(), StatusReserva.ATIVA)) {
            throw new RegraNegocioException("O usuario ja possui uma reserva ativa para este livro");
        }

        Reserva reserva = new Reserva(livro, usuario, Instant.now());
        return ReservaResponse.from(reservaRepository.save(reserva));
    }

    @Transactional
    public ReservaResponse cancelar(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Reserva", id));
        if (reserva.getStatus() != StatusReserva.ATIVA && reserva.getStatus() != StatusReserva.ATENDIDA) {
            throw new RegraNegocioException("Apenas reservas ativas ou atendidas podem ser canceladas");
        }
        reserva.cancelar();
        return ReservaResponse.from(reserva);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReservaResponse> listarPorUsuario(Long usuarioId, Pageable pageable) {
        return PageResponse.from(
                reservaRepository.findByUsuarioId(usuarioId, pageable).map(ReservaResponse::from));
    }

    @Transactional(readOnly = true)
    public PageResponse<ReservaResponse> listar(Pageable pageable) {
        return PageResponse.from(reservaRepository.findAll(pageable).map(ReservaResponse::from));
    }
}
