package com.fiap.biblioteca.service;

import com.fiap.biblioteca.domain.Emprestimo;
import com.fiap.biblioteca.domain.Livro;
import com.fiap.biblioteca.domain.Reserva;
import com.fiap.biblioteca.domain.Usuario;
import com.fiap.biblioteca.domain.enums.StatusEmprestimo;
import com.fiap.biblioteca.domain.enums.StatusReserva;
import com.fiap.biblioteca.dto.PageResponse;
import com.fiap.biblioteca.dto.emprestimo.EmprestimoRequest;
import com.fiap.biblioteca.dto.emprestimo.EmprestimoResponse;
import com.fiap.biblioteca.exception.RecursoNaoEncontradoException;
import com.fiap.biblioteca.exception.RegraNegocioException;
import com.fiap.biblioteca.repository.EmprestimoRepository;
import com.fiap.biblioteca.repository.LivroRepository;
import com.fiap.biblioteca.repository.ReservaRepository;
import com.fiap.biblioteca.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;
    private final int diasPrazo;
    private final int diasExpiracaoReserva;

    public EmprestimoService(EmprestimoRepository emprestimoRepository,
                             LivroRepository livroRepository,
                             UsuarioRepository usuarioRepository,
                             ReservaRepository reservaRepository,
                             @Value("${biblioteca.emprestimo.dias-prazo}") int diasPrazo,
                             @Value("${biblioteca.reserva.dias-expiracao}") int diasExpiracaoReserva) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroRepository = livroRepository;
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
        this.diasPrazo = diasPrazo;
        this.diasExpiracaoReserva = diasExpiracaoReserva;
    }

    @Transactional
    public EmprestimoResponse emprestar(EmprestimoRequest req) {
        Livro livro = livroRepository.findById(req.livroId())
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Livro", req.livroId()));
        Usuario usuario = usuarioRepository.findById(req.usuarioId())
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Usuario", req.usuarioId()));

        if (!usuario.isAtivo()) {
            throw new RegraNegocioException("Usuario inativo nao pode realizar emprestimos");
        }
        if (!livro.isDisponivel()) {
            throw new RegraNegocioException("Nao ha exemplares disponiveis do livro: " + livro.getTitulo());
        }

        livro.emprestarExemplar();
        Instant agora = Instant.now();
        // data prevista = hoje + prazo (em UTC). prazo vem do application.yml
        LocalDate previsao = LocalDate.now(ZoneOffset.UTC).plusDays(diasPrazo);
        Emprestimo emprestimo = new Emprestimo(livro, usuario, agora, previsao);
        return EmprestimoResponse.from(emprestimoRepository.save(emprestimo));
    }

    @Transactional
    public EmprestimoResponse devolver(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Emprestimo", emprestimoId));
        if (emprestimo.getStatus() == StatusEmprestimo.DEVOLVIDO) {
            throw new RegraNegocioException("Este emprestimo ja foi devolvido");
        }

        // System.out.println("devolvendo emprestimo " + emprestimoId);
        emprestimo.registrarDevolucao(LocalDate.now(ZoneOffset.UTC));
        Livro livro = emprestimo.getLivro();
        livro.devolverExemplar();

        // devolveu o livro? entao a primeira pessoa da fila de reserva ja pode ser avisada
        reservaRepository
                .findFirstByLivroIdAndStatusOrderByDataReservaAsc(livro.getId(), StatusReserva.ATIVA)
                .ifPresent(r -> r.atender(LocalDate.now(ZoneOffset.UTC).plusDays(diasExpiracaoReserva)));

        return EmprestimoResponse.from(emprestimo);
    }

    @Transactional(readOnly = true)
    public PageResponse<EmprestimoResponse> listar(StatusEmprestimo status, Pageable pageable) {
        Page<Emprestimo> pagina = (status == null)
                ? emprestimoRepository.findAll(pageable)
                : emprestimoRepository.findByStatus(status, pageable);
        return PageResponse.from(pagina.map(EmprestimoResponse::from));
    }

    @Transactional(readOnly = true)
    public EmprestimoResponse buscarPorId(Long id) {
        Emprestimo emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Emprestimo", id));
        return EmprestimoResponse.from(emprestimo);
    }
}
