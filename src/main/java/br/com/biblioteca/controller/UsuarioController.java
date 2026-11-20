package br.com.biblioteca.controller;

import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.service.UsuarioService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // Dados recebidos na edição. A senha é opcional.
    public record DadosEdicao(
        @NotBlank(message = "Informe o nome")
        String nome,

        @NotBlank(message = "Informe o usuário")
        String login,

        String senha
    ) {
    }

    @GetMapping("/usuarios")
    public List<Usuario> listar() {
        return service.listar();
    }

    @PostMapping("/usuarios")
    public Usuario criar(@Valid @RequestBody Usuario usuario) {
        return service.criar(usuario);
    }

    @PutMapping("/usuarios/{id}")
    public Usuario editar(
        @PathVariable Long id,
        @Valid @RequestBody DadosEdicao dados
    ) {
        return service.editar(
            id,
            dados.nome(),
            dados.login(),
            dados.senha()
        );
    }

    @DeleteMapping("/usuarios/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }

    @GetMapping("/sessao")
    public Map<String, Object> sessao(
        Principal principal,
        Authentication autenticacao
    ) {
        boolean admin = autenticacao.getAuthorities().stream()
            .anyMatch(
                permissao -> permissao.getAuthority()
                    .equals("ROLE_ADMIN")
            );

        return Map.of(
            "login", principal.getName(),
            "admin", admin
        );
    }
}