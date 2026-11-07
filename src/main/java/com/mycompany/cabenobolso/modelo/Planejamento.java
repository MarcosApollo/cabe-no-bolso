package com.mycompany.cabenobolso.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Planejamento implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private Map<Integer, Participante> participantes = new TreeMap<>();
    private List<Despesa> despesas = new ArrayList<>();

    public Planejamento(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do planejamento vazio");
        }
        this.nome = nome;
    }

    // ---------- Participantes ----------

    public void cadastrarParticipante(Participante p) {
        if (p == null) {
            throw new IllegalArgumentException("Participante inválido");
        }
        if (participantes.containsKey(p.getCodigo())) {
            throw new IllegalArgumentException("Código já existe " + p.getCodigo());
        }
        participantes.put(p.getCodigo(), p);
    }

    public Participante buscarParticipante(int codigo) {
        return participantes.get(codigo);
    }

    public List<Participante> getParticipantes() {
        return List.copyOf(participantes.values());
    }

    public void excluirParticipante(int codigo) {
        if (!participantes.containsKey(codigo)){
            throw new IllegalArgumentException("Participante inexistente: " + codigo);
        }
        for (Despesa d : despesas) {
            if (d.getParticipantes().contains(codigo)) {
                throw new IllegalArgumentException("Participante possui despesas vinculadas: " + codigo);
            }
        }
        participantes.remove(codigo);
    }

    // ---------- Despesas ----------

    private void validarDespesa(Despesa d) {
        if (d == null) {
            throw new IllegalArgumentException("Despesa inválida");
        }
        for (int codigo : d.getParticipantes()) {
            if (!participantes.containsKey(codigo)) {
                throw new IllegalArgumentException("Participante não cadastrado: " + codigo);
            }
        }
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= despesas.size()) {
            throw new IllegalArgumentException("Despesa inexistente: " + indice);
        }
    }

    public void incluirDespesa(Despesa d) {
        validarDespesa(d);
        despesas.add(d);
    }

    public void editarDespesa(int indice, Despesa nova) {
        validarIndice(indice);
        validarDespesa(nova);
        despesas.set(indice, nova);
    }

    public void excluirDespesa(int indice) {
        validarIndice(indice);
        despesas.remove(indice);
    }

    // ---------- Consultas ----------

    public String getNome() {
        return this.nome;
    }

    public List<Despesa> getDespesas() {
        return List.copyOf(this.despesas);
    }
}