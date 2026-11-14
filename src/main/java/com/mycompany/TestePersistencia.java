package com.mycompany;

import com.mycompany.cabenobolso.modelo.Planejamento;
import com.mycompany.cabenobolso.modelo.Participante;
import com.mycompany.cabenobolso.persistencia.ArquivoPersistencia;

public class TestePersistencia {

    public static void main(String[] args) {

        Planejamento planejamento =
                new Planejamento("Viagem");

        Participante p1 =
                new Participante(1, "George", 100000);

        planejamento.cadastrarParticipante(p1);

        ArquivoPersistencia persistencia =
                new ArquivoPersistencia();

        persistencia.salvar(planejamento);

        Planejamento carregado =
                persistencia.carregar();

        System.out.println(
                "Planejamento: "
                        + carregado.getNome()
        );

        System.out.println(
                "Participantes: "
                        + carregado.getParticipantes().size()
        );
    }
}