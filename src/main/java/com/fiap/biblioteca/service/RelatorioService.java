package com.fiap.biblioteca.service;

import com.fiap.biblioteca.dto.report.EmprestimoAtivoResponse;
import com.fiap.biblioteca.dto.report.LivroMaisEmprestadoResponse;
import com.fiap.biblioteca.repository.EmprestimoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class RelatorioService {

    private final EmprestimoRepository emprestimoRepository;

    public RelatorioService(EmprestimoRepository emprestimoRepository) {
        this.emprestimoRepository = emprestimoRepository;
    }

    // Top 20 mais emprestados. a contagem/ordenacao acontece no banco, aqui so devolve
    @Transactional(readOnly = true)
    public List<LivroMaisEmprestadoResponse> livrosMaisEmprestados() {
        return emprestimoRepository.findLivrosMaisEmprestados(PageRequest.of(0, 20));
    }

    @Transactional(readOnly = true)
    public List<EmprestimoAtivoResponse> emprestimosAtivos() {
        LocalDate hoje = LocalDate.now(ZoneOffset.UTC);
        return emprestimoRepository.findEmprestimosEmAberto().stream()
                .map(emp -> EmprestimoAtivoResponse.from(emp, hoje))
                .toList();
    }
}
