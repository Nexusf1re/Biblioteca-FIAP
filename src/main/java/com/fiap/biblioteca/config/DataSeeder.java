package com.fiap.biblioteca.config;

import com.fiap.biblioteca.domain.Livro;
import com.fiap.biblioteca.domain.Usuario;
import com.fiap.biblioteca.domain.enums.TipoUsuario;
import com.fiap.biblioteca.dto.emprestimo.EmprestimoRequest;
import com.fiap.biblioteca.repository.LivroRepository;
import com.fiap.biblioteca.repository.UsuarioRepository;
import com.fiap.biblioteca.service.EmprestimoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

// popula uns dados de exemplo pra facilitar a demo. so roda com o profile "seed" ligado
@Component
@Profile("seed")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmprestimoService emprestimoService;

    public DataSeeder(LivroRepository livroRepository, UsuarioRepository usuarioRepository,
                      EmprestimoService emprestimoService) {
        this.livroRepository = livroRepository;
        this.usuarioRepository = usuarioRepository;
        this.emprestimoService = emprestimoService;
    }

    @Override
    public void run(String... args) {
        if (livroRepository.count() > 0) {
            log.info("Seed ignorado: a base ja contem dados.");
            return;
        }
        log.info("Populando base com dados de demonstracao...");

        List<Livro> livros = livroRepository.saveAll(List.of(
                new Livro("Clean Code", "Robert C. Martin", "9780132350884", "Prentice Hall", 2008, "Engenharia de Software", 3),
                new Livro("Effective Java", "Joshua Bloch", "9780134685991", "Addison-Wesley", 2018, "Java", 2),
                new Livro("Domain-Driven Design", "Eric Evans", "9780321125217", "Addison-Wesley", 2003, "Arquitetura", 2),
                new Livro("Refactoring", "Martin Fowler", "9780134757599", "Addison-Wesley", 2018, "Engenharia de Software", 1),
                new Livro("The Pragmatic Programmer", "Andrew Hunt", "9780201616224", "Addison-Wesley", 1999, "Carreira", 2)
        ));

        List<Usuario> usuarios = usuarioRepository.saveAll(List.of(
                new Usuario("Ana Souza", "ana.souza@fiap.com.br", TipoUsuario.ALUNO),
                new Usuario("Bruno Lima", "bruno.lima@fiap.com.br", TipoUsuario.PROFESSOR),
                new Usuario("Carla Dias", "carla.dias@fiap.com.br", TipoUsuario.MEMBRO)
        ));

        // Gera alguns emprestimos para alimentar os relatorios.
        emprestimoService.emprestar(new EmprestimoRequest(livros.get(0).getId(), usuarios.get(0).getId()));
        emprestimoService.emprestar(new EmprestimoRequest(livros.get(0).getId(), usuarios.get(1).getId()));
        emprestimoService.emprestar(new EmprestimoRequest(livros.get(1).getId(), usuarios.get(2).getId()));
        emprestimoService.emprestar(new EmprestimoRequest(livros.get(3).getId(), usuarios.get(0).getId()));

        log.info("Seed concluido: {} livros, {} usuarios.", livros.size(), usuarios.size());
    }
}
