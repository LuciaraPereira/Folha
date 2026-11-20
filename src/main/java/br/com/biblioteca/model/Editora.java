package br.com.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Editora {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @NotBlank
    public String nome;
}
