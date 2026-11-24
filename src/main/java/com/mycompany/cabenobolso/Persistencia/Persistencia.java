package com.mycompany.cabenobolso.persistencia;

import com.mycompany.cabenobolso.modelo.Planejamento;

public interface Persistencia {

    void salvar(Planejamento planejamento);

    Planejamento carregar();
}