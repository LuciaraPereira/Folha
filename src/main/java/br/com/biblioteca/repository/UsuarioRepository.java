package br.com.biblioteca.repository;

import br.com.biblioteca.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
java.util.Optional<br.com.biblioteca.model.Usuario> findByLogin(String login);
}