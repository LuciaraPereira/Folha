package br.com.biblioteca.controller;

import br.com.biblioteca.service.IsbnService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/isbn")
public class IsbnController {
    private final IsbnService service;

    public IsbnController(IsbnService s) {
        service = s;
    }

    @GetMapping("/{isbn}")
    public Map<?, ?> buscar(@PathVariable String isbn) {
        return service.buscar(isbn);
    }
}