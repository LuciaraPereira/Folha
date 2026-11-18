package br.com.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @NotBlank
    public String titulo;
    @NotBlank
    @Column(unique = true)
    public String isbn;
    @Min(1)
    public int exemplares = 1;
    @NotNull
    @ManyToOne(optional = false)
    public Autor autor;
    @NotNull
    @ManyToOne(optional = false)
    public Categoria categoria;
    @NotNull
    @ManyToOne(optional = false)
    public Editora editora;
}
