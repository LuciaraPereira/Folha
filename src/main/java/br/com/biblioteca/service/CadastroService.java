package br.com.biblioteca.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CadastroService {
    public <T> List<T> listar(JpaRepository<T, Long> repo) {
        return repo.findAll();
    }

    public <T> T buscar(JpaRepository<T, Long> repo, Long id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro não encontrado"));
    }

    public <T> T salvar(JpaRepository<T, Long> repo, T item) {
        return repo.save(item);
    }

    public <T> void excluir(JpaRepository<T, Long> repo, Long id) {
        buscar(repo, id);
        repo.deleteById(id);
    }
}
