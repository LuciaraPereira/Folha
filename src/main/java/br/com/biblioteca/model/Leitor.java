package br.com.biblioteca.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
public class Leitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank
    @Pattern(regexp = "[0-9]{11}", message = "Informe um CPF com 11 numeros")
    @Column(unique = true, length = 11)
    public String cpf;

    @NotBlank
    public String nome;

    @NotBlank
    @Email
    public String email;

    @NotBlank
    public String telefone;

    @JsonSetter("cpf")
    public void setCpf(String valor) {
        this.cpf = valor == null ? null : valor.replaceAll("[.\\-\\s]", "");
    }
}