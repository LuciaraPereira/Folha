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

    // =========================
    // AUTORES
    // =========================

    @GetMapping("/autores")
    public List<Autor> listarAutor() {
        return service.listar(autores);
    }

    @GetMapping("/autores/{id}")
    public Autor buscarAutor(@PathVariable Long id) {
        return service.buscar(autores, id);
    }

    @PostMapping("/autores")
    public Autor criarAutor(@Valid @RequestBody Autor item) {
        item.id = null;
        return service.salvar(autores, item);
    }

    @PutMapping("/autores/{id}")
    public Autor editarAutor(
            @PathVariable Long id,
            @Valid @RequestBody Autor item
    ) {
        service.buscar(autores, id);
        item.id = id;

        return service.salvar(autores, item);
    }

    @DeleteMapping("/autores/{id}")
    public void excluirAutor(@PathVariable Long id) {
        service.excluir(autores, id);
    }

    // =========================
    // CATEGORIAS
    // =========================

    @GetMapping("/categorias")
    public List<Categoria> listarCategoria() {
        return service.listar(categorias);
    }

    @GetMapping("/categorias/{id}")
    public Categoria buscarCategoria(@PathVariable Long id) {
        return service.buscar(categorias, id);
    }

    @PostMapping("/categorias")
    public Categoria criarCategoria(
            @Valid @RequestBody Categoria item
    ) {
        item.id = null;
        return service.salvar(categorias, item);
    }

    @PutMapping("/categorias/{id}")
    public Categoria editarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody Categoria item
    ) {
        service.buscar(categorias, id);
        item.id = id;

        return service.salvar(categorias, item);
    }

    @DeleteMapping("/categorias/{id}")
    public void excluirCategoria(@PathVariable Long id) {
        service.excluir(categorias, id);
    }

    // =========================
    // EDITORAS
    // =========================

    @GetMapping("/editoras")
    public List<Editora> listarEditora() {
        return service.listar(editoras);
    }

    @GetMapping("/editoras/{id}")
    public Editora buscarEditora(@PathVariable Long id) {
        return service.buscar(editoras, id);
    }

    @PostMapping("/editoras")
    public Editora criarEditora(
            @Valid @RequestBody Editora item
    ) {
        item.id = null;
        return service.salvar(editoras, item);
    }

    @PutMapping("/editoras/{id}")
    public Editora editarEditora(
            @PathVariable Long id,
            @Valid @RequestBody Editora item
    ) {
        service.buscar(editoras, id);
        item.id = id;

        return service.salvar(editoras, item);
    }

    @DeleteMapping("/editoras/{id}")
    public void excluirEditora(@PathVariable Long id) {
        service.excluir(editoras, id);
    }

    // =========================
    // LEITORES
    // =========================

    @GetMapping("/leitores")
    public List<Leitor> listarLeitor() {
        return service.listar(leitores);
    }

    @GetMapping("/leitores/{id}")
    public Leitor buscarLeitor(@PathVariable Long id) {
        return service.buscar(leitores, id);
    }

    @PostMapping("/leitores")
    public Leitor criarLeitor(
            @Valid @RequestBody Leitor item
    ) {
        item.id = null;
        return salvarLeitor(item);
    }

    @PutMapping("/leitores/{id}")
    public Leitor editarLeitor(
            @PathVariable Long id,
            @Valid @RequestBody Leitor item
    ) {
        service.buscar(leitores, id);
        item.id = id;

        return salvarLeitor(item);
    }

    @DeleteMapping("/leitores/{id}")
    public void excluirLeitor(@PathVariable Long id) {

        service.excluir(leitores, id);
    }

    private Leitor salvarLeitor(Leitor item) {
        // Remove pontos, traço e outros caracteres do CPF.
        item.cpf = item.cpf.replaceAll("\\D", "");

        leitores.findByCpf(item.cpf).ifPresent(outro -> {
            if (!outro.id.equals(item.id)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "CPF já cadastrado para outro leitor"
                );
            }
        });

        return service.salvar(leitores, item);
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
        if (item.id != null && item.exemplares < emprestimos.countByLivroIdAndDataDevolucaoIsNull(item.id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A quantidade total não pode ser menor que a quantidade já emprestada.");
        }
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

}