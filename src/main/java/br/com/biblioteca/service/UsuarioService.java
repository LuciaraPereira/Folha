package br.com.biblioteca.service;

import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public UsuarioService(
        UsuarioRepository repo,
        PasswordEncoder encoder
    ) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public List<Usuario> listar() {
        return repo.findAll();
    }

    public Usuario criar(Usuario usuario) {
        if (repo.findByLogin(usuario.login).isPresent()) {
            throw erro("Este nome de usuário já está cadastrado");
        }

        usuario.id = null;
        usuario.perfil = "BIBLIOTECARIO";
        usuario.senha = encoder.encode(usuario.senha);

        return repo.save(usuario);
    }

    public Usuario editar(
        Long id,
        String nome,
        String login,
        String novaSenha
    ) {
        Usuario usuario = buscarBibliotecario(id);

        repo.findByLogin(login).ifPresent(outro -> {
            if (!outro.id.equals(id)) {
                throw erro("Este nome de usuário já está cadastrado");
            }
        });

        usuario.nome = nome;
        usuario.login = login;

        // Se a senha estiver vazia, mantém a senha atual.
        if (novaSenha != null && !novaSenha.isBlank()) {
            usuario.senha = encoder.encode(novaSenha);
        }

        return repo.save(usuario);
    }

    public void excluir(Long id) {
        Usuario usuario = buscarBibliotecario(id);
        repo.delete(usuario);
    }

    private Usuario buscarBibliotecario(Long id) {
        Usuario usuario = repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Bibliotecário não encontrado"
            ));

        if (!"BIBLIOTECARIO".equals(usuario.perfil)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Esta opção permite alterar apenas bibliotecários"
            );
        }

        return usuario;
    }

    private ResponseStatusException erro(String mensagem) {
        return new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            mensagem
        );
    }
}