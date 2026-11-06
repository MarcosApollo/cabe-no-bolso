package com.mycompany.cabenobolso.modelo;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public abstract class Despesa implements Serializable {
    private static final long serialVersionUID = 1L;

    private String descricao;
    private long valorCentavos;
    private Set<Integer> participantes = new TreeSet<>();

    public Despesa(String descricao, long valorCentavos, Set<Integer> codigos) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição vazia");
        }
        if (valorCentavos <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero");
        }
        if (codigos == null || codigos.isEmpty()) {
            throw new IllegalArgumentException("A despesa precisa de participante");
        }
        this.descricao = descricao;
        this.valorCentavos = valorCentavos;
        this.participantes.addAll(codigos);
    }

    public String getDescricao() { return descricao; }
    public long getValorCentavos() { return  valorCentavos;}

    public Set<Integer> getParticipantes() {
        return Collections.unmodifiableSet(participantes);
    }

    public abstract Map<Integer, Long> ratear();
}
