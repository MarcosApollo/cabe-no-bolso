package com.mycompany.cabenobolso;

import com.mycompany.cabenobolso.modelo.Participante;
import com.mycompany.cabenobolso.modelo.Planejamento;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Tela de resumo: mostra o total gasto e a situação orçamentária de cada participante.
 * Os valores são sempre calculados a partir do Planejamento, nunca guardados na tela.
 */
public class TelaResumo extends JPanel {

    private final Planejamento planejamento;
    private final JTextArea areaTexto;

    public TelaResumo(Planejamento planejamento) {
        if (planejamento == null) {
            throw new IllegalArgumentException("Planejamento inválido");
        }
        this.planejamento = planejamento;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(new JLabel("Resumo do planejamento", SwingConstants.LEFT), BorderLayout.NORTH);

        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        add(new JScrollPane(areaTexto), BorderLayout.CENTER);

        atualizar();
    }

    /** Recarrega os valores do modelo. Chamada pelo controller ao abrir esta aba. */
    public void atualizar() {
        Map<Integer, Long> totais = planejamento.calcularTotais();

        StringBuilder texto = new StringBuilder();
        texto.append("Planejamento: ").append(planejamento.getNome()).append("\n");
        texto.append("Custo total: R$ ").append(formatar(planejamento.getCustoTotal())).append("\n\n");
        texto.append(String.format("%-20s %14s %14s %22s %14s%n",
                "Participante", "Total (R$)", "Orçamento", "Situação", "Excedente"));
        texto.append("-".repeat(88)).append("\n");

        for (Participante p : planejamento.getParticipantes()) {
            texto.append(String.format("%-20s %14s %14s %22s %14s%n",
                    limitar(p.getNome(), 20),
                    formatar(totais.get(p.getCodigo())),
                    formatar(p.getOrcamentoCentavos()),
                    planejamento.consultarSituacaoOrcamento(p.getCodigo()),
                    formatar(planejamento.calcularExcedente(p.getCodigo()))));
        }

        if (planejamento.getParticipantes().isEmpty()) {
            texto.append("Nenhum participante cadastrado.\n");
            texto.append("Cadastre participantes e despesas para ver o resumo.");
        }

        areaTexto.setText(texto.toString());
    }

    private String limitar(String texto, int tamanho) {
        return texto.length() <= tamanho ? texto : texto.substring(0, tamanho - 1) + "...";
    }

    private String formatar(long centavos) {
        return String.format("%.2f", centavos / 100.0).replace(".", ",");
    }
}
