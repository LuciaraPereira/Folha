package br.com.biblioteca.controller;

import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.service.EmprestimoService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/emprestimos")
public class EmprestimoController {

    private final EmprestimoService service;

    public EmprestimoController(EmprestimoService service) {
        this.service = service;
    }

    // Dados recebidos para renovar um empréstimo.
    public record Renovacao(LocalDate novaData) {
    }

    @GetMapping
    public List<Emprestimo> listar() {
        return service.listar();
    }

    @PostMapping
    public Emprestimo criar(@RequestBody Emprestimo item) {
        return service.criar(item);
    }