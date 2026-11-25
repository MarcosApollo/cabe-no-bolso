package com.mycompany.cabenobolso;

import com.mycompany.cabenobolso.TelasParticipantes.TelaDespesas;
import com.mycompany.cabenobolso.modelo.Planejamento;
import com.mycompany.cabenobolso.persistencia.ArquivoPersistencia;
import com.mycompany.cabenobolso.persistencia.Persistencia;
import com.mycompany.cabenobolso.telasparticipantes.TelaParticipantes;

import javax.swing.*;
import java.awt.*;

/**
 * Controller: integra as telas do sistema, mantém um único Planejamento partilhado
 * e liga os botões de salvar/carregar à camada de persistência.
 */
public class PlanejamentoController {

    private static final String ARQUIVO = "planejamento.dat";

    private Planejamento planejamento;
    private final Persistencia persistencia = new ArquivoPersistencia();

    private final JFrame janela;
    private JPanel painelCentral;
    private JTabbedPane abas;
    private TelaParticipantes telaParticipantes;
    private TelaDespesas telaDespesas;
    private TelaResumo telaResumo;

    public PlanejamentoController(Planejamento planejamento) {
        if (planejamento == null) {
            throw new IllegalArgumentException("Planejamento inválido");
        }
        this.planejamento = planejamento;

        janela = new JFrame("Cabe no Bolso");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setLayout(new BorderLayout(10, 10));
        ((JComponent) janela.getContentPane()).setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10));

        painelCentral = new JPanel(new BorderLayout());
        janela.add(painelCentral, BorderLayout.CENTER);
        janela.add(criarPainelBotoes(), BorderLayout.SOUTH);

        montarAbas();

        janela.setSize(950, 620);
        janela.setLocationRelativeTo(null);
    }

    public void exibir() {
        janela.setVisible(true);
    }

    public Planejamento getPlanejamento() {
        return planejamento;
    }

    private void montarAbas() {
        telaParticipantes = new TelaParticipantes(planejamento);
        telaDespesas = new TelaDespesas(planejamento);
        telaResumo = new TelaResumo(planejamento);

        abas = new JTabbedPane();
        abas.addTab("Participantes", telaParticipantes);
        abas.addTab("Despesas", telaDespesas);
        abas.addTab("Resumo", telaResumo);
        abas.addChangeListener(e -> aoTrocarAba());

        painelCentral.removeAll();
        painelCentral.add(abas, BorderLayout.CENTER);
        painelCentral.revalidate();
        painelCentral.repaint();
    }

    private void aoTrocarAba() {
        int selecionada = abas.getSelectedIndex();
        if (selecionada == 1) {
            // Sincroniza participantes e despesas com o modelo partilhado.
            telaDespesas.atualizarTela();
        } else if (selecionada == 2) {
            telaResumo.atualizar();
        }
    }

    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCarregar = new JButton("Carregar");
        JButton btnNovo = new JButton("Novo planejamento");

        btnSalvar.addActionListener(e -> salvar());
        btnCarregar.addActionListener(e -> carregar());
        btnNovo.addActionListener(e -> novoPlanejamento());

        painel.add(btnSalvar);
        painel.add(btnCarregar);
        painel.add(btnNovo);
        return painel;
    }

    public void salvar() {
        persistencia.salvar(planejamento);
        JOptionPane.showMessageDialog(janela,
                "Planejamento salvo em " + ARQUIVO + ".",
                "Cabe no Bolso", JOptionPane.INFORMATION_MESSAGE);
    }

    public void carregar() {
        Planejamento carregado = persistencia.carregar();
        if (carregado == null) {
            JOptionPane.showMessageDialog(janela,
                    "Não foi possível carregar " + ARQUIVO + ".",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        this.planejamento = carregado;
        montarAbas();
        JOptionPane.showMessageDialog(janela, "Planejamento carregado.",
                "Cabe no Bolso", JOptionPane.INFORMATION_MESSAGE);
    }

    public void novoPlanejamento() {
        String nome = JOptionPane.showInputDialog(janela,
                "Nome do planejamento:", "Novo planejamento", JOptionPane.QUESTION_MESSAGE);
        if (nome == null) {
            return;
        }
        this.planejamento = new Planejamento(nome.isBlank() ? "Novo planejamento" : nome.trim());
        montarAbas();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Persistencia persistencia = new ArquivoPersistencia();
            Planejamento planejamento = persistencia.carregar();
            if (planejamento == null) {
                planejamento = new Planejamento("Cabe no Bolso");
            }
            new PlanejamentoController(planejamento).exibir();
        });
    }
}
