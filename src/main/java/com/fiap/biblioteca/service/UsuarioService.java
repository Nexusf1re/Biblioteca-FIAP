package com.fiap.biblioteca.service;

import com.fiap.biblioteca.domain.Usuario;
import com.fiap.biblioteca.domain.enums.StatusEmprestimo;
import com.fiap.biblioteca.dto.PageResponse;
import com.fiap.biblioteca.dto.emprestimo.EmprestimoResponse;
import com.fiap.biblioteca.dto.usuario.UsuarioRequest;
import com.fiap.biblioteca.dto.usuario.UsuarioResponse;
import com.fiap.biblioteca.exception.RecursoNaoEncontradoException;
import com.fiap.biblioteca.exception.RegraNegocioException;
import com.fiap.biblioteca.repository.EmprestimoRepository;
import com.fiap.biblioteca.repository.ReservaRepository;
import com.fiap.biblioteca.repository.UsuarioRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmprestimoRepository emprestimoRepository;
    private final ReservaRepository reservaRepository;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          EmprestimoRepository emprestimoRepository,
                          ReservaRepository reservaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.emprestimoRepository = emprestimoRepository;
        this.reservaRepository = reservaRepository;
    }

    @Transactional
    public UsuarioResponse criar(UsuarioRequest req) {
        if (usuarioRepository.existsByEmail(req.email())) {
            throw new RegraNegocioException("Ja existe um usuario cadastrado com o e-mail " + req.email());
        }
        Usuario usuario = new Usuario(req.nome(), req.email(), req.tipo());
        if (req.ativo() != null) {
            usuario.setAtivo(req.ativo());
        }
        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public PageResponse<UsuarioResponse> listar(Pageable pageable) {
        return PageResponse.from(usuarioRepository.findAll(pageable).map(UsuarioResponse::from));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return UsuarioResponse.from(obter(id));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest req) {
        Usuario usuario = obter(id);
        if (usuarioRepository.existsByEmailAndIdNot(req.email(), id)) {
            throw new RegraNegocioException("Ja existe outro usuario cadastrado com o e-mail " + req.email());
        }
        usuario.setNome(req.nome());
        usuario.setEmail(req.email());
        usuario.setTipo(req.tipo());
        // 'ativo' e opcional no PUT: se vier nulo, mantem o valor atual; envie false para inativar
        if (req.ativo() != null) {
            usuario.setAtivo(req.ativo());
        }
        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public void excluir(Long id) {
        Usuario usuario = obter(id);
        // emprestimos ativos = caso mais comum, mensagem mais especifica
        if (emprestimoRepository.existsByUsuarioIdAndStatus(id, StatusEmprestimo.ATIVO)) {
            throw new RegraNegocioException("Nao e possivel excluir um usuario que possui emprestimos ativos");
        }
        // emprestimos ja devolvidos continuam referenciando o usuario (FK); apagar perderia o historico
        if (emprestimoRepository.existsByUsuarioId(id)) {
            throw new RegraNegocioException(
                    "Nao e possivel excluir um usuario que possui historico de emprestimos. "
                            + "Se necessario, inative-o via PUT /api/usuarios/" + id + " com \"ativo\": false");
        }
        if (reservaRepository.existsByUsuarioId(id)) {
            throw new RegraNegocioException(
                    "Nao e possivel excluir um usuario que possui reservas registradas. "
                            + "Se necessario, inative-o via PUT /api/usuarios/" + id + " com \"ativo\": false");
        }
        usuarioRepository.delete(usuario);
    }

    // historico = todos os emprestimos do usuario (paginado pra nao trazer tudo de uma vez)
    @Transactional(readOnly = true)
    public PageResponse<EmprestimoResponse> historicoEmprestimos(Long usuarioId, Pageable pageable) {
        obter(usuarioId); // so pra estourar 404 se o usuario nao existir
        return PageResponse.from(
                emprestimoRepository.findByUsuarioId(usuarioId, pageable).map(EmprestimoResponse::from));
    }

    private Usuario obter(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Usuario", id));
    }
}
