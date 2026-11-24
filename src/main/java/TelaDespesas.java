package view;

import com.mycompany.cabenobolso.modelo.Despesa;
import com.mycompany.cabenobolso.modelo.DespesaFixa;
import com.mycompany.cabenobolso.modelo.DespesaPorPessoa;
import com.mycompany.cabenobolso.modelo.Participante;
import com.mycompany.cabenobolso.modelo.Planejamento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;

/**
 * Tela de despesas: cadastrar, editar e excluir despesas do Planejamento.
 * A lista de participantes e a tabela são sempre recarregadas a partir do modelo,
 * para que esta tela e a TelaParticipantes partilhem os mesmos dados.
 */
public class TelaDespesas extends JPanel {

    private final Planejamento planejamento;

    private JTextField txtDescricao;
    private JTextField txtValor;
    private JComboBox<String> cbTipo;

    private DefaultListModel<String> listModelParticipantes;
    private JList<String> listParticipantes;
    // Código de cada participante na mesma ordem em que aparece na JList.
    private List<Integer> codigosParticipantes = new ArrayList<>();

    private JButton btnIncluir;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnLimpar;

    private JTable tabelaDespesas;
    private DefaultTableModel tableModel;
    // Participantes de cada linha da tabela, na mesma ordem das linhas.
    private List<Set<Integer>> participantesPorLinha = new ArrayList<>();

    /**
     * Construtor usado pela aplicação: recebe o Planejamento partilhado.
     */
    public TelaDespesas(Planejamento planejamento) {
        if (planejamento == null) {
            throw new IllegalArgumentException("Planejamento inválido");
        }
        this.planejamento = planejamento;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(criarPainelFormulario(), BorderLayout.NORTH);
        add(criarPainelTabela(), BorderLayout.CENTER);

        configurarEventos();
        atualizarParticipantes();
        atualizarTabela();
        limparCampos();
    }

    /**
     * Construtor apenas para testar a tela isoladamente.
     * Na aplicação, usar o construtor que recebe o Planejamento.
     */
    public TelaDespesas() {
        this(new Planejamento("Teste"));
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Cadastro de Despesa"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Descrição
        gbc.gridx = 0;
        gbc.gridy = 0;
        painel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtDescricao = new JTextField(20);
        painel.add(txtDescricao, gbc);

        // Valor
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        painel.add(new JLabel("Valor (R$):"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtValor = new JTextField(10);
        painel.add(txtValor, gbc);

        // Tipo
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        painel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cbTipo = new JComboBox<>(new String[]{"Fixa", "Por pessoa"});
        painel.add(cbTipo, gbc);

        // Participantes (seleção múltipla)
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        painel.add(new JLabel("Participantes (CTRL+Clique):"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        listModelParticipantes = new DefaultListModel<>();
        listParticipantes = new JList<>(listModelParticipantes);
        listParticipantes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollLista = new JScrollPane(listParticipantes);
        scrollLista.setPreferredSize(new Dimension(200, 70));
        painel.add(scrollLista, gbc);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnIncluir = new JButton("Incluir");
        btnEditar = new JButton("Salvar Edição");
        btnExcluir = new JButton("Excluir");
        btnLimpar = new JButton("Limpar");
        painelBotoes.add(btnIncluir);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnLimpar);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        painel.add(painelBotoes, gbc);

        return painel;
    }

    private JScrollPane criarPainelTabela() {
        String[] colunas = {"ID", "Descrição", "Tipo", "Valor (R$)", "Participantes"};

        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaDespesas = new JTable(tableModel);
        tabelaDespesas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return new JScrollPane(tabelaDespesas);
    }

    private void configurarEventos() {
        btnIncluir.addActionListener(e -> incluir());
        btnEditar.addActionListener(e -> editar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparCampos());

        // Ao clicar numa linha, carrega os dados no formulário para editar.
        tabelaDespesas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarLinhaSelecionada();
            }
        });
    }

    // ---------- Ações ----------

    private void incluir() {
        String descricao = txtDescricao.getText().trim();
        if (descricao.isEmpty()) {
            aviso("Informe a descrição da despesa.");
            txtDescricao.requestFocus();
            return;
        }

        long valor = lerValor();
        if (valor < 0) {
            return;
        }

        Set<Integer> codigos = codigosSelecionados();
        if (codigos.isEmpty()) {
            aviso("Selecione ao menos um participante.");
            return;
        }

        try {
            planejamento.incluirDespesa(criarDespesa(descricao, valor, codigos));
            atualizarTabela();
            limparCampos();
            JOptionPane.showMessageDialog(this, "Despesa cadastrada.");
        } catch (IllegalArgumentException ex) {
            aviso(ex.getMessage());
        }
    }

    private void editar() {
        int indice = indiceSelecionado();
        if (indice < 0) {
            return;
        }

        String descricao = txtDescricao.getText().trim();
        if (descricao.isEmpty()) {
            aviso("Informe a descrição da despesa.");
            txtDescricao.requestFocus();
            return;
        }

        long valor = lerValor();
        if (valor < 0) {
            return;
        }

        Set<Integer> codigos = codigosSelecionados();
        if (codigos.isEmpty()) {
            aviso("Selecione ao menos um participante.");
            return;
        }

        try {
            planejamento.editarDespesa(indice, criarDespesa(descricao, valor, codigos));
            atualizarTabela();
            limparCampos();
            JOptionPane.showMessageDialog(this, "Despesa atualizada.");
        } catch (IllegalArgumentException ex) {
            aviso(ex.getMessage());
        }
    }

    private void excluir() {
        int indice = indiceSelecionado();
        if (indice < 0) {
            return;
        }

        String descricao = tableModel.getValueAt(indice, 1).toString();
        int resposta = JOptionPane.showConfirmDialog(this,
                "Excluir a despesa \"" + descricao + "\"?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            planejamento.excluirDespesa(indice);
            atualizarTabela();
            limparCampos();
            JOptionPane.showMessageDialog(this, "Despesa excluída.");
        } catch (IllegalArgumentException ex) {
            aviso(ex.getMessage());
        }
    }

    // ---------- Integração com o modelo ----------

    /**
     * Recarrega a lista de participantes. Deve ser chamada pela tela principal
     * depois de cadastrar/editar/excluir um participante.
     */
    public void atualizarParticipantes() {
        listModelParticipantes.clear();
        codigosParticipantes.clear();
        for (Participante p : planejamento.getParticipantes()) {
            codigosParticipantes.add(p.getCodigo());
            listModelParticipantes.addElement(p.getCodigo() + " - " + p.getNome());
        }
        listParticipantes.clearSelection();
    }

    /**
     * Recarrega a tabela com as despesas do Planejamento.
     */
    public void atualizarTabela() {
        tableModel.setRowCount(0);
        participantesPorLinha.clear();

        int indice = 0;
        for (Despesa d : planejamento.getDespesas()) {
            Set<Integer> codigos = d.getParticipantes();
            participantesPorLinha.add(codigos);

            String tipo = d instanceof DespesaFixa ? "Fixa" : "Por pessoa";
            tableModel.addRow(new Object[]{
                    indice + 1,
                    d.getDescricao(),
                    tipo,
                    formatar(d.getValorCentavos()),
                    nomesParticipantes(codigos)
            });
            indice++;
        }
    }

    private Despesa criarDespesa(String descricao, long valorCentavos, Set<Integer> codigos) {
        if ("Fixa".equals(cbTipo.getSelectedItem())) {
            return new DespesaFixa(descricao, valorCentavos, codigos);
        }
        return new DespesaPorPessoa(descricao, valorCentavos, codigos);
    }

    private Set<Integer> codigosSelecionados() {
        Set<Integer> codigos = new TreeSet<>();
        for (int i : listParticipantes.getSelectedIndices()) {
            codigos.add(codigosParticipantes.get(i));
        }
        return codigos;
    }

    private int indiceSelecionado() {
        int linha = tabelaDespesas.getSelectedRow();
        if (linha < 0) {
            aviso("Selecione uma despesa na tabela.");
            return -1;
        }
        return tabelaDespesas.convertRowIndexToModel(linha);
    }

    private void carregarLinhaSelecionada() {
        int linha = tabelaDespesas.getSelectedRow();
        if (linha < 0 || linha >= participantesPorLinha.size()) {
            return;
        }
        int indice = tabelaDespesas.convertRowIndexToModel(linha);

        txtDescricao.setText(tableModel.getValueAt(indice, 1).toString());
        cbTipo.setSelectedItem(tableModel.getValueAt(indice, 2).toString());
        txtValor.setText(tableModel.getValueAt(indice, 3).toString());

        Set<Integer> codigos = participantesPorLinha.get(indice);
        listParticipantes.clearSelection();
        for (int i = 0; i < codigosParticipantes.size(); i++) {
            if (codigos.contains(codigosParticipantes.get(i))) {
                listParticipantes.addSelectionInterval(i, i);
            }
        }

        btnIncluir.setEnabled(false);
        btnEditar.setEnabled(true);
        btnExcluir.setEnabled(true);
    }

    private void limparCampos() {
        txtDescricao.setText("");
        txtValor.setText("");
        cbTipo.setSelectedIndex(0);
        listParticipantes.clearSelection();
        tabelaDespesas.clearSelection();

        btnIncluir.setEnabled(true);
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
        txtDescricao.requestFocus();
    }

    // ---------- Formatação ----------

    // Aceita 1500 / 1500,50 / 1.500,50 / R$ 1500. Devolve centavos, ou -1 se inválido.
    private long lerValor() {
        String texto = txtValor.getText().replace("R$", "").trim().replace(" ", "");

        if (texto.isEmpty()) {
            aviso("Informe o valor da despesa.");
            txtValor.requestFocus();
            return -1;
        }

        if (texto.contains(",")) {
            texto = texto.replace(".", "").replace(",", ".");
        }

        double valor;
        try {
            valor = Double.parseDouble(texto);
        } catch (NumberFormatException ex) {
            aviso("Valor inválido. Exemplo: 1500,00");
            txtValor.requestFocus();
            return -1;
        }

        if (valor <= 0) {
            aviso("O valor deve ser maior que zero.");
            txtValor.requestFocus();
            return -1;
        }

        return Math.round(valor * 100);
    }

    private String nomesParticipantes(Set<Integer> codigos) {
        StringJoiner joiner = new StringJoiner(", ");
        for (int codigo : codigos) {
            Participante p = planejamento.buscarParticipante(codigo);
            joiner.add(p == null ? String.valueOf(codigo) : p.getNome());
        }
        return joiner.toString();
    }

    private String formatar(long centavos) {
        return String.format("%.2f", centavos / 100.0).replace(".", ",");
    }

    private void aviso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    // Só para testar a tela isoladamente.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Planejamento plano = new Planejamento("Teste");
            plano.cadastrarParticipante(new Participante(1, "Ana", 100000));
            plano.cadastrarParticipante(new Participante(2, "Bruno", 50000));
            plano.cadastrarParticipante(new Participante(3, "Carla", 50000));

            JFrame janela = new JFrame("Cabe no Bolso - Despesas");
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.add(new TelaDespesas(plano));
            janela.setSize(750, 500);
            janela.setLocationRelativeTo(null);
            janela.setVisible(true);
        });
    }
}
