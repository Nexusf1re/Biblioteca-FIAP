package com.fiap.biblioteca.service;

import com.fiap.biblioteca.domain.enums.StatusEmprestimo;
import com.fiap.biblioteca.domain.enums.StatusReserva;
import com.fiap.biblioteca.domain.enums.TipoUsuario;
import com.fiap.biblioteca.dto.emprestimo.EmprestimoRequest;
import com.fiap.biblioteca.dto.emprestimo.EmprestimoResponse;
import com.fiap.biblioteca.dto.livro.LivroRequest;
import com.fiap.biblioteca.dto.livro.LivroResponse;
import com.fiap.biblioteca.dto.report.LivroMaisEmprestadoResponse;
import com.fiap.biblioteca.dto.reserva.ReservaRequest;
import com.fiap.biblioteca.dto.reserva.ReservaResponse;
import com.fiap.biblioteca.dto.usuario.UsuarioRequest;
import com.fiap.biblioteca.dto.usuario.UsuarioResponse;
import com.fiap.biblioteca.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// fluxo de ponta a ponta no H2: emprestar, devolver, reserva e os relatorios
@SpringBootTest
@Transactional
class EmprestimoFluxoIntegrationTest {

    @Autowired
    private LivroService livroService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private EmprestimoService emprestimoService;
    @Autowired
    private ReservaService reservaService;
    @Autowired
    private RelatorioService relatorioService;

    private static final AtomicInteger SEQ = new AtomicInteger(1);

    private LivroResponse novoLivro(int total) {
        int n = SEQ.getAndIncrement();
        String isbn = String.format("978%010d", n);
        return livroService.criar(new LivroRequest("Livro " + n, "Autor " + n, isbn, "Ed", 2020, "Cat", total));
    }

    private UsuarioResponse novoUsuario() {
        int n = SEQ.getAndIncrement();
        return usuarioService.criar(new UsuarioRequest("Usuario " + n, "user" + n + "@fiap.com", TipoUsuario.ALUNO));
    }

    @Test
    void emprestarDecrementaEDevolverIncrementaDisponibilidade() {
        LivroResponse livro = novoLivro(1);
        UsuarioResponse usuario = novoUsuario();

        EmprestimoResponse emp = emprestimoService.emprestar(new EmprestimoRequest(livro.id(), usuario.id()));
        assertThat(emp.status()).isEqualTo(StatusEmprestimo.ATIVO);
        assertThat(livroService.buscarPorId(livro.id()).quantidadeDisponivel()).isZero();

        EmprestimoResponse dev = emprestimoService.devolver(emp.id());
        assertThat(dev.status()).isEqualTo(StatusEmprestimo.DEVOLVIDO);
        assertThat(dev.dataDevolucaoReal()).isEqualTo(LocalDate.now(ZoneOffset.UTC));
        assertThat(livroService.buscarPorId(livro.id()).quantidadeDisponivel()).isEqualTo(1);
    }

    @Test
    void previsaoDeDevolucaoEhCalculadaEmQuatorzeDias() {
        LivroResponse livro = novoLivro(1);
        UsuarioResponse usuario = novoUsuario();

        EmprestimoResponse emp = emprestimoService.emprestar(new EmprestimoRequest(livro.id(), usuario.id()));
        assertThat(emp.dataPrevistaDevolucao()).isEqualTo(LocalDate.now(ZoneOffset.UTC).plusDays(14));
    }

    @Test
    void emprestimoSemExemplarDisponivelEhRejeitado() {
        LivroResponse livro = novoLivro(1);
        UsuarioResponse u1 = novoUsuario();
        UsuarioResponse u2 = novoUsuario();

        emprestimoService.emprestar(new EmprestimoRequest(livro.id(), u1.id()));
        assertThatThrownBy(() -> emprestimoService.emprestar(new EmprestimoRequest(livro.id(), u2.id())))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void reservaEhAtendidaAoDevolverLivro() {
        LivroResponse livro = novoLivro(1);
        UsuarioResponse u1 = novoUsuario();
        UsuarioResponse u2 = novoUsuario();

        EmprestimoResponse emp = emprestimoService.emprestar(new EmprestimoRequest(livro.id(), u1.id()));
        ReservaResponse reserva = reservaService.reservar(new ReservaRequest(livro.id(), u2.id()));
        assertThat(reserva.status()).isEqualTo(StatusReserva.ATIVA);

        emprestimoService.devolver(emp.id());

        ReservaResponse aposDevolucao = reservaService.listarPorUsuario(u2.id(),
                org.springframework.data.domain.PageRequest.of(0, 10)).content().get(0);
        assertThat(aposDevolucao.status()).isEqualTo(StatusReserva.ATENDIDA);
    }

    @Test
    void relatorioLivrosMaisEmprestadosRetornaOrdenadoPorQuantidade() {
        LivroResponse popular = novoLivro(5);
        LivroResponse menosPopular = novoLivro(5);

        // 3 emprestimos do livro popular, 1 do menos popular
        for (int i = 0; i < 3; i++) {
            emprestimoService.emprestar(new EmprestimoRequest(popular.id(), novoUsuario().id()));
        }
        emprestimoService.emprestar(new EmprestimoRequest(menosPopular.id(), novoUsuario().id()));

        List<LivroMaisEmprestadoResponse> relatorio = relatorioService.livrosMaisEmprestados();
        assertThat(relatorio).isNotEmpty();
        LivroMaisEmprestadoResponse top = relatorio.get(0);
        assertThat(top.livroId()).isEqualTo(popular.id());
        assertThat(top.totalEmprestimos()).isEqualTo(3L);
    }
}
