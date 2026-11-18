package br.com.biblioteca.controller;

import br.com.biblioteca.model.Autor;
import br.com.biblioteca.model.Categoria;
import br.com.biblioteca.model.Editora;
import br.com.biblioteca.model.Leitor;
import br.com.biblioteca.model.Livro;

import br.com.biblioteca.repository.AutorRepository;
import br.com.biblioteca.repository.CategoriaRepository;
import br.com.biblioteca.repository.EditoraRepository;
import br.com.biblioteca.repository.EmprestimoRepository;
import br.com.biblioteca.repository.LeitorRepository;
import br.com.biblioteca.repository.LivroRepository;

import br.com.biblioteca.service.CadastroService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class CadastroController {

    private final AutorRepository autores;
    private final CategoriaRepository categorias;
    private final EditoraRepository editoras;
    private final LeitorRepository leitores;
    private final LivroRepository livros;
    private final CadastroService service;
    private final EmprestimoRepository emprestimos;

    public CadastroController(
            AutorRepository autores,
            CategoriaRepository categorias,
            EditoraRepository editoras,
            LeitorRepository leitores,
            LivroRepository livros,
            EmprestimoRepository emprestimos,
            CadastroService service
    ) {
        this.autores = autores;
        this.categorias = categorias;
        this.editoras = editoras;
        this.leitores = leitores;
        this.livros = livros;
        this.emprestimos = emprestimos;
        this.service = service;
    }
}