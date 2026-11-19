package br.com.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @ManyToOne(optional = false)
    public Livro livro;

    @NotNull
    @ManyToOne(optional = false)
    public Leitor leitor;

    @NotNull
    public LocalDate dataEmprestimo;

    @NotNull
    public LocalDate dataPrevista;

    public LocalDate dataDevolucao;

    public Boolean multaPaga = false;

    public LocalDate dataUltimoPagamento;

    @Transient
    public BigDecimal getValorMulta() {
        if (dataPrevista == null) {
            return BigDecimal.ZERO;
        }

        // Preserva multas já pagas na versão anterior do sistema.
        if (dataDevolucao != null
                && Boolean.TRUE.equals(multaPaga)
                && dataUltimoPagamento == null) {
            return BigDecimal.ZERO;
        }

        LocalDate dataFinal = dataDevolucao == null
                ? LocalDate.now()
                : dataDevolucao;

        LocalDate inicioContagem = dataPrevista;

        if (dataUltimoPagamento != null
                && dataUltimoPagamento.isAfter(inicioContagem)) {
            inicioContagem = dataUltimoPagamento;
        }

        long diasAtraso = Math.max(
                0,
                ChronoUnit.DAYS.between(inicioContagem, dataFinal)
        );

        // Valor da multa: R$ 1,00 por dia.
        return BigDecimal.valueOf(diasAtraso)
                .multiply(new BigDecimal("1.00"));
    }
}