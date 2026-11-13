package com.mycompany.cabenobolso.modelo; // Classe que representa a tela de gerenciamento de participantes

import javax.swing.*; // Importa a biblioteca Swing para criar a interface gráfica
import javax.swing.table.DefaultTableModel; // Importa a classe DefaultTableModel para gerenciar os dados da tabela
import java.awt.*; // Importa a biblioteca AWT para gerenciar o layout e os eventos da interface gráfica

// Tela de participantes: cadastrar, editar, excluir e listar.
// Os participantes ficam guardados no Planejamento, assim a tela de despesas usa os mesmos.
public class TelaParticipantes extends JPanel {

    private Planejamento planejamento;

    private JTextField txtCodigo;
    private JTextField txtNome;
    private JTextField txtOrcamento;

    private JButton btnIncluir;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnLimpar;

    private JTable tabelaParticipantes;
    private DefaultTableModel tableModel;

    public TelaParticipantes(Planejamento planejamento) {
        this.planejamento = planejamento;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // formulario em cima e tabela embaixo
        add(criarPainelFormulario(), BorderLayout.NORTH);
        add(criarPainelTabela(), BorderLayout.CENTER);

        configurarEventos();
        atualizarTabela();
        limparCampos();
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Cadastro de Participante"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // codigo (o sistema que gera, por isso nao da pra editar)
        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtCodigo = new JTextField(6);
        txtCodigo.setEditable(false);
        painel.add(txtCodigo, gbc);

        // nome
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        painel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtNome = new JTextField(20);
        painel.add(txtNome, gbc);

        // orcamento
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        painel.add(new JLabel("Orçamento (R$):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtOrcamento = new JTextField(10);
        painel.add(txtOrcamento, gbc);

        // botoes
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnIncluir = new JButton("Incluir");
        btnEditar = new JButton("Salvar Edição");
        btnExcluir = new JButton("Excluir");
        btnLimpar = new JButton("Limpar");
        painelBotoes.add(btnIncluir);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnLimpar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        painel.add(painelBotoes, gbc);

        return painel;
    }

    private JScrollPane criarPainelTabela() {
        String[] colunas = {"Código", "Nome", "Orçamento (R$)"};

        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // nao deixa editar direto na tabela, so pelo formulario
                return false;
            }
        };

        tabelaParticipantes = new JTable(tableModel);
        tabelaParticipantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return new JScrollPane(tabelaParticipantes);
    }

    private void configurarEventos() {
        btnIncluir.addActionListener(e -> incluir());
        btnEditar.addActionListener(e -> editar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparCampos());

        // quando clica numa linha, joga os dados no formulario pra poder editar
        tabelaParticipantes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarLinhaSelecionada();
            }
        });
    }

    private void incluir() {
        String nome = txtNome.getText().trim();
        if (!nomeValido(nome, -1)) {
            return;
        }
        long orcamento = lerOrcamento();
        if (orcamento < 0) {
            return;
        }

        try {
            Participante p = new Participante(proximoCodigo(), nome, orcamento);
            planejamento.cadastrarParticipante(p);
            atualizarTabela();
            limparCampos();
            JOptionPane.showMessageDialog(this, "Participante cadastrado.");
        } catch (IllegalArgumentException ex) {
            aviso(ex.getMessage());
        }
    }

    private void editar() {
        int linha = tabelaParticipantes.getSelectedRow();
        if (linha < 0) {
            aviso("Selecione um participante na tabela para editar.");
            return;
        }
        int codigo = (int) tableModel.getValueAt(linha, 0);

        String nome = txtNome.getText().trim();
        // passa o codigo pra nao acusar nome repetido com ele mesmo
        if (!nomeValido(nome, codigo)) {
            return;
        }
        long orcamento = lerOrcamento();
        if (orcamento < 0) {
            return;
        }

        try {
            planejamento.alterarParticipante(codigo, nome, orcamento);
            atualizarTabela();
            limparCampos();
            JOptionPane.showMessageDialog(this, "Participante atualizado.");
        } catch (IllegalArgumentException ex) {
            aviso(ex.getMessage());
        }
    }

    private void excluir() {
        int linha = tabelaParticipantes.getSelectedRow();
        if (linha < 0) {
            aviso("Selecione um participante na tabela para excluir.");
            return;
        }
        int codigo = (int) tableModel.getValueAt(linha, 0);
        String nome = tableModel.getValueAt(linha, 1).toString();

        int resp = JOptionPane.showConfirmDialog(this, "Excluir o participante " + nome + "?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // o Planejamento nao deixa excluir se a pessoa estiver em alguma despesa
            planejamento.excluirParticipante(codigo);
            atualizarTabela();
            limparCampos();
            JOptionPane.showMessageDialog(this, "Participante excluído.");
        } catch (IllegalArgumentException ex) {
            aviso("Exclusão não realizada. O participante está vinculado a uma despesa.");
        }
    }

    // recarrega a tabela com os participantes do planejamento
    // (publico pra outra tela poder chamar depois de mudar alguma coisa)
    public void atualizarTabela() {
        tableModel.setRowCount(0);
        for (Participante p : planejamento.getParticipantes()) {
            tableModel.addRow(new Object[]{p.getCodigo(), p.getNome(), formatar(p.getOrcamentoCentavos())});
        }
    }

    private void carregarLinhaSelecionada() {
        int linha = tabelaParticipantes.getSelectedRow();
        if (linha < 0) {
            return;
        }
        txtCodigo.setText(tableModel.getValueAt(linha, 0).toString());
        txtNome.setText(tableModel.getValueAt(linha, 1).toString());
        txtOrcamento.setText(tableModel.getValueAt(linha, 2).toString());

        // modo edicao
        btnIncluir.setEnabled(false);
        btnEditar.setEnabled(true);
        btnExcluir.setEnabled(true);
    }

    private void limparCampos() {
        tabelaParticipantes.clearSelection();
        txtCodigo.setText(String.valueOf(proximoCodigo()));
        txtNome.setText("");
        txtOrcamento.setText("");

        // volta pro modo cadastro
        btnIncluir.setEnabled(true);
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
        txtNome.requestFocus();
    }

    // codigoAtual = -1 quando for cadastro novo
    private boolean nomeValido(String nome, int codigoAtual) {
        if (nome.isEmpty()) {
            aviso("Informe o nome do participante.");
            txtNome.requestFocus();
            return false;
        }
        if (nome.length() > 60) {
            aviso("O nome deve ter no máximo 60 caracteres.");
            txtNome.requestFocus();
            return false;
        }
        for (Participante p : planejamento.getParticipantes()) {
            if (p.getCodigo() != codigoAtual && p.getNome().equalsIgnoreCase(nome)) {
                aviso("Já existe um participante com esse nome.");
                txtNome.requestFocus();
                return false;
            }
        }
        return true;
    }

    // le o orcamento digitado e devolve em centavos (-1 se tiver algo errado)
    // aceita 1500 / 1500,50 / 1.500,50 / R$ 1500
    private long lerOrcamento() {
        String texto = txtOrcamento.getText().replace("R$", "").trim();

        if (texto.isEmpty()) {
            aviso("Informe o orçamento do participante.");
            txtOrcamento.requestFocus();
            return -1;
        }

        // se tem virgula, o ponto e de milhar
        if (texto.contains(",")) {
            texto = texto.replace(".", "").replace(",", ".");
        }

        double valor;
        try {
            valor = Double.parseDouble(texto);
        } catch (NumberFormatException ex) {
            aviso("Orçamento inválido. Exemplo: 1500,00");
            txtOrcamento.requestFocus();
            return -1;
        }

        if (valor < 0) {
            aviso("O orçamento não pode ser negativo.");
            txtOrcamento.requestFocus();
            return -1;
        }

        // guarda em centavos igual o Participante
        return Math.round(valor * 100);
    }

    // maior codigo + 1
    private int proximoCodigo() {
        int maior = 0;
        for (Participante p : planejamento.getParticipantes()) {
            if (p.getCodigo() > maior) {
                maior = p.getCodigo();
            }
        }
        return maior + 1;
    }

    // 150050 -> 1500,50
    private String formatar(long centavos) {
        return String.format("%.2f", centavos / 100.0).replace(".", ",");
    }

    private void aviso(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    // so pra testar a tela sozinha
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame janela = new JFrame("Cabe no Bolso - Participantes");
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.add(new TelaParticipantes(new Planejamento("Teste")));
            janela.setSize(600, 450);
            janela.setLocationRelativeTo(null);
            janela.setVisible(true);
        });
    }
}
