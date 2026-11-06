package com.mycompany.cabenobolso.modelo;

import java.io.Serializable;

// Inicio da classe
public class Participante implements Serializable {
    private static final long serialVersionUID = 1L;

    private int codigo;
    private String nome;
    private long orcamentoCentavos;

    public Participante(int codigo, String nome, long orcamentoCentavos) {
        if (codigo <= 0) {
            throw new IllegalArgumentException("O código deve ser maior que zero");
        }
        this.codigo = codigo;
        setNome(nome);
        setOrcamentoCentavos(orcamentoCentavos);
    }

    // Método Getters
    public int getCodigo() {
        return this.codigo;
    }

    public String getNome() {
        return this.nome;
    }

    public long getOrcamentoCentavos() {
        return this.orcamentoCentavos;
    }

    // Método Setter
    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome vazio");
        }
        this.nome = nome;
    }

    public void setOrcamentoCentavos(long orcamentoCentavos) {
        if (orcamentoCentavos < 0) {
            throw new IllegalArgumentException("O orçamento não pode ser negativo");
        }
        this.orcamentoCentavos = orcamentoCentavos;
    }
}
