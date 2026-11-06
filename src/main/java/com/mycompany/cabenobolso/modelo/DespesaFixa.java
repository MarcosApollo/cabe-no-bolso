package com.mycompany.cabenobolso.modelo;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DespesaFixa extends Despesa {
    private static final long serialVersionUID = 1L;


    public DespesaFixa(String descricao, long valorCentavos, Set<Integer> codigos) {
        super(descricao, valorCentavos, codigos);
    }

    @Override
    public Map<Integer, Long> ratear() {
        Map<Integer, Long> resultado = new TreeMap<>();
        int n = getParticipantes().size();
        long base = getValorCentavos() / n;
        long resto = getValorCentavos() % n;

        for (int codigo : getParticipantes()) {
            long valor = base;
            if (resto > 0) {
                valor = valor + 1;
                resto = resto - 1;
            }
            resultado.put(codigo, valor);
        }
        return resultado;
    }
}
