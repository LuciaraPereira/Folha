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

























































































































































































// =========================
    // LIVROS
    // =========================

    @GetMapping("/livros")
    public List<Livro> listarLivro() {
        return service.listar(livros);
    }

    @GetMapping("/livros/{id}")
    public Livro buscarLivro(@PathVariable Long id) {
        return service.buscar(livros, id);
    }

    @PostMapping("/livros")
    public Livro criarLivro(
            @Valid @RequestBody Livro item
    ) {
        item.id = null;
        return salvarLivro(item);
    }

    @PutMapping("/livros/{id}")
    public Livro editarLivro(
            @PathVariable Long id,
            @Valid @RequestBody Livro item
    ) {
        service.buscar(livros, id);
        item.id = id;

        return salvarLivro(item);
    }

    @DeleteMapping("/livros/{id}")
    public void excluirLivro(@PathVariable Long id) {
        service.excluir(livros, id);
    }

    private Livro salvarLivro(Livro item) {
      


      
        // Remove espaços e traços para comparar o ISBN.
        String isbnInformado = item.isbn
                .replaceAll("[-\\s]", "")
                .toUpperCase(Locale.ROOT);

        for (Livro cadastrado : livros.findAll()) {
            String isbnCadastrado = cadastrado.isbn
                    .replaceAll("[-\\s]", "")
                    .toUpperCase(Locale.ROOT);

            boolean mesmoIsbn = isbnCadastrado.equals(isbnInformado);

            // Na edição, o próprio livro pode manter seu ISBN.
            boolean outroLivro = !Objects.equals(
                    cadastrado.id,
                    item.id
            );

            if (mesmoIsbn && outroLivro) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Este ISBN já está cadastrado. "
                                + "Procure o livro na lista para editar "

                );
            }
        }

        // Salva o ISBN sem espaços e sem traços.
        item.isbn = isbnInformado;

        return service.salvar(livros, item);
    }