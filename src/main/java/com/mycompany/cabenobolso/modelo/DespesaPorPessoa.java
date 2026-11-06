package com.mycompany.cabenobolso.modelo;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DespesaPorPessoa extends Despesa {
    private static final long serialVersionUID = 1L;


    public DespesaPorPessoa(String descricao, long valorCentavos, Set<Integer> codigos) {
        super(descricao, valorCentavos, codigos);
    }

    @Override
    public Map<Integer, Long> ratear() {
        Map<Integer, Long> resultado = new TreeMap<>();
        long valor = getValorCentavos();
        for (int codigo : getParticipantes()) {
            resultado.put(codigo, valor);
        }
        return resultado;
    }
}
