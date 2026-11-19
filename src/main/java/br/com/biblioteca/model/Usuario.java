package br.com.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;
@Entity public class Usuario {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
@NotBlank public String nome;
@NotBlank @Column(unique=true) public String login;
@JsonProperty(access=JsonProperty.Access.WRITE_ONLY) @NotBlank public String senha;
public String perfil;
}