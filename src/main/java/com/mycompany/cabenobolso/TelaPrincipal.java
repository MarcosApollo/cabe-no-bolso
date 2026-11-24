package com.mycompany.cabenobolso;

import com.mycompany.cabenobolso.modelo.Participante;
import com.mycompany.cabenobolso.modelo.Planejamento;
import com.mycompany.cabenobolso.persistencia.ArquivoPersistencia;
import com.mycompany.cabenobolso.persistencia.Persistencia;
import com.mycompany.cabenobolso.telasparticipantes.TelaParticipantes;
import view.TelaDespesas;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Janela principal: junta as telas de participantes e despesas, mostra o resumo
 * e permite salvar/carregar o planejamento usando a camada de persistência.
 */
public class TelaPrincipal extends JFrame {

    private static final String ARQUIVO = "planejamento.dat";

    private Planejamento planejamento;
    private final Persistencia persistencia = new ArquivoPersistencia();

    private JPanel painelCentral;
    private JTabbedPane abas;
    private TelaParticipantes telaParticipantes;
    private TelaDespesas telaDespesas;
    private JTextArea areaResumo;

    public TelaPrincipal(Planejamento planejamento) {
        if (planejamento == null) {
            throw new IllegalArgumentException("Planejamento inválido");
        }
        this.planejamento = planejamento;

        super.setTitle("Cabe no Bolso");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        painelCentral = new JPanel(new BorderLayout());
        add(painelCentral, BorderLayout.CENTER);
        add(criarPainelBotoes(), BorderLayout.SOUTH);

        montarAbas();

        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    private void montarAbas() {
        telaParticipantes = new TelaParticipantes(planejamento);
        telaDespesas = new TelaDespesas(planejamento);
        areaResumo = new JTextArea();
        areaResumo.setEditable(false);
        areaResumo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        abas = new JTabbedPane();
        abas.addTab("Participantes", telaParticipantes);
        abas.addTab("Despesas", telaDespesas);
        abas.addTab("Resumo", new JScrollPane(areaResumo));
        abas.addChangeListener(e -> aoTrocarAba());

        painelCentral.removeAll();
        painelCentral.add(abas, BorderLayout.CENTER);
        painelCentral.revalidate();
        painelCentral.repaint();

        atualizarResumo();
    }

    private void aoTrocarAba() {
        int selecionada = abas.getSelectedIndex();
        if (selecionada == 1) {
            // Participantes podem ter mudado enquanto a aba de despesas estava escondida.
            telaDespesas.atualizarParticipantes();
            telaDespesas.atualizarTabela();
        } else if (selecionada == 2) {
            atualizarResumo();
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

    private void salvar() {
        persistencia.salvar(planejamento);
        JOptionPane.showMessageDialog(this,
                "Planejamento salvo em " + ARQUIVO + ".",
                "Cabe no Bolso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void carregar() {
        Planejamento carregado = persistencia.carregar();
        if (carregado == null) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível carregar o arquivo " + ARQUIVO + ".",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        this.planejamento = carregado;
        montarAbas();
        JOptionPane.showMessageDialog(this, "Planejamento carregado.",
                "Cabe no Bolso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void novoPlanejamento() {
        String nome = JOptionPane.showInputDialog(this,
                "Nome do planejamento:", "Novo planejamento", JOptionPane.QUESTION_MESSAGE);
        if (nome == null) {
            return;
        }
        this.planejamento = new Planejamento(nome.isBlank() ? "Novo planejamento" : nome.trim());
        montarAbas();
    }

    private void atualizarResumo() {
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
            texto.append("Nenhum participante cadastrado.");
        }

        areaResumo.setText(texto.toString());
    }

    private String limitar(String texto, int tamanho) {
        return texto.length() <= tamanho ? texto : texto.substring(0, tamanho - 1) + "…";
    }

    private String formatar(long centavos) {
        return String.format("%.2f", centavos / 100.0).replace(".", ",");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Persistencia persistencia = new ArquivoPersistencia();
            Planejamento planejamento = persistencia.carregar();
            if (planejamento == null) {
                planejamento = new Planejamento("Cabe no Bolso");
            }
            new TelaPrincipal(planejamento).setVisible(true);
        });
    }
}
