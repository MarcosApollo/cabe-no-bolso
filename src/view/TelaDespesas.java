// Define que esta classe pertence ao pacote 'view' (camada visual do padrão MVC)
package view;

// Importa todos os componentes visuais do Swing (JPanel, JButton, JTextField, JTable, etc.)
import javax.swing.*;
// Importa o modelo padrão de dados usado para controlar as linhas e colunas da JTable
import javax.swing.table.DefaultTableModel;
// Importa classes de layout e dimensões gráficas (BorderLayout, GridBagLayout, Insets, Dimension)
import java.awt.*;
// Importa a interface List para manipular coleções de textos (participantes selecionados)
import java.util.List;

// Declara a classe TelaDespesas herdando de JPanel (para poder ser colocada dentro de uma aba ou janela)
public class TelaDespesas extends JPanel {

    // Campo de texto para o usuário digitar o nome/descrição da despesa
    private JTextField txtDescricao;
    // Campo de texto para o usuário digitar o valor numérico da despesa
    private JTextField txtValor;
    // Caixa de seleção suspensa para escolher o tipo ("Fixa" ou "Por pessoa")
    private JComboBox<String> cbTipo;
    // Modelo de dados que guarda os itens exibidos dentro da lista de participantes
    private DefaultListModel<String> listModelParticipantes;
    // Componente visual de lista que exibe os participantes para seleção múltipla
    private JList<String> listParticipantes;

    // Botão para adicionar uma nova despesa
    private JButton btnIncluir;
    // Botão para salvar as alterações de uma despesa selecionada na tabela
    private JButton btnEditar;
    // Botão para remover uma despesa selecionada na tabela
    private JButton btnExcluir;
    // Botão para apagar o que está escrito nos campos do formulário
    private JButton btnLimpar;

    // Componente visual de tabela para exibir todas as despesas cadastradas
    private JTable tabelaDespesas;
    // Modelo de dados que gerencia as colunas e linhas da tabelaDespesas
    private DefaultTableModel tableModel;

    // Construtor da classe: executado automaticamente quando 'new TelaDespesas()' é chamado
    public TelaDespesas() {
        // Define o gerenciador de layout principal como BorderLayout com 10 pixels de espaçamento
        setLayout(new BorderLayout(10, 10));
        // Cria uma margem interna invisível de 10 pixels nas quatro bordas do painel
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Adiciona o painel do formulário na região superior (NORTH) da tela
        add(criarPainelFormulario(), BorderLayout.NORTH);
        // Adiciona o painel da tabela na região central (CENTER), ocupando o espaço restante
        add(criarPainelTabela(), BorderLayout.CENTER);
        // Chama o método que ativa os eventos internos de interface (clique na tabela e limpar)
        configurarEventosBasicos();
    }

    // Método privado que constrói e organiza todos os campos e botões do formulário
    private JPanel criarPainelFormulario() {
        // Cria um subpainel usando GridBagLayout (permite alinhar itens em grade flexível)
        JPanel painel = new JPanel(new GridBagLayout());
        // Adiciona uma borda visível com título ao redor do formulário
        painel.setBorder(BorderFactory.createTitledBorder("Cadastro de Despesa"));

        // Cria o objeto de restrições que controla posição (x, y) e tamanho de cada item na grade
        GridBagConstraints gbc = new GridBagConstraints();
        // Define um espaçamento externo de 5 pixels em todos os lados de cada componente
        gbc.insets = new Insets(5, 5, 5, 5);
        // Faz com que os componentes estiquem horizontalmente para preencher a largura da célula
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- LINHA 0: CAMPO DESCRIÇÃO ---
        // Posiciona na coluna 0, linha 0
        gbc.gridx = 0; gbc.gridy = 0;
        // Adiciona o rótulo de texto "Descrição:"
        painel.add(new JLabel("Descrição:"), gbc);

        // Posiciona na coluna 1 e dá peso 1.0 para o campo esticar quando a janela crescer
        gbc.gridx = 1; gbc.weightx = 1.0;
        // Instancia a caixa de texto com largura base de 20 colunas
        txtDescricao = new JTextField(20);
        // Adiciona a caixa de texto ao painel
        painel.add(txtDescricao, gbc);

        // --- LINHA 1: CAMPO VALOR ---
        // Volta para a coluna 0, desce para a linha 1 e reseta o peso de esticamento para 0
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        // Adiciona o rótulo de texto "Valor (R$):"
        painel.add(new JLabel("Valor (R$):"), gbc);

        // Vai para a coluna 1 com peso 1.0 para esticar
        gbc.gridx = 1; gbc.weightx = 1.0;
        // Instancia a caixa de texto do valor
        txtValor = new JTextField(10);
        // Adiciona a caixa de texto ao painel
        painel.add(txtValor, gbc);

        // --- LINHA 2: CAIXA DE SELEÇÃO DE TIPO ---
        // Coluna 0, linha 2, sem esticar
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        // Adiciona o rótulo "Tipo:"
        painel.add(new JLabel("Tipo:"), gbc);

        // Coluna 1, com esticamento horizontal
        gbc.gridx = 1; gbc.weightx = 1.0;
        // Instancia o menu suspenso já preenchido com as duas opções exigidas no projeto
        cbTipo = new JComboBox<>(new String[]{"Fixa", "Por pessoa"});
        // Adiciona o menu suspenso ao painel
        painel.add(cbTipo, gbc);

        // --- LINHA 3: LISTA DE PARTICIPANTES ---
        // Coluna 0, linha 3, sem esticar
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        // Adiciona o rótulo orientando o usuário a segurar CTRL para selecionar várias pessoas
        painel.add(new JLabel("Participantes (CTRL+Clique):"), gbc);

        // Coluna 1, com esticamento horizontal
        gbc.gridx = 1; gbc.weightx = 1.0;
        // Instancia o modelo que armazenará os nomes dos participantes
        listModelParticipantes = new DefaultListModel<>();
        // Instancia a lista visual vinculada ao modelo acima
        listParticipantes = new JList<>(listModelParticipantes);
        // Configura a lista para permitir selecionar múltiplos itens simultaneamente
        listParticipantes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        // Coloca a lista dentro de um painel com barra de rolagem (caso haja muitos participantes)
        JScrollPane scrollLista = new JScrollPane(listParticipantes);
        // Define a altura fixa de 70 pixels para a caixa da lista não ficar grande demais
        scrollLista.setPreferredSize(new Dimension(200, 70));
        // Adiciona a lista com barra de rolagem ao painel
        painel.add(scrollLista, gbc);

        // --- LINHA 4: PAINEL DE BOTÕES ---
        // Cria um subpainel horizontal alinhado à esquerda (FlowLayout.LEFT) para os botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // Instancia cada um dos quatro botões com seus respectivos textos
        btnIncluir = new JButton("Incluir");
        btnEditar = new JButton("Salvar Edição");
        btnExcluir = new JButton("Excluir");
        btnLimpar = new JButton("Limpar");

        // Adiciona os botões lado a lado dentro do painel de botões
        painelBotoes.add(btnIncluir);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnLimpar);

        // Posiciona na coluna 0, linha 4, e faz ocupar 2 colunas de largura (gridwidth = 2)
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        // Adiciona a barra de botões ao formulário
        painel.add(painelBotoes, gbc);

        // Retorna o formulário completamente montado
        return painel;
    }

    // Método privado que cria e configura a tabela onde as despesas serão exibidas
    private JScrollPane criarPainelTabela() {
        // Define os títulos das colunas da tabela
        String[] colunas = {"ID", "Descrição", "Tipo", "Valor (R$)", "Participantes"};

        // Cria o modelo da tabela com as colunas definidas e 0 linhas iniciais
        tableModel = new DefaultTableModel(colunas, 0) {
            // Sobrescreve a regra de edição da célula
            @Override
            public boolean isCellEditable(int row, int column) {
                // Retorna falso para impedir que o usuário digite direto dentro da tabela (força usar o formulário)
                return false;
            }
        };

        // Instancia a tabela visual conectada ao modelo de dados
        tabelaDespesas = new JTable(tableModel);
        // Configura a tabela para permitir selecionar apenas uma despesa por vez
        tabelaDespesas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Envolve a tabela em um JScrollPane (necessário no Swing para exibir o cabeçalho e barra de rolagem)
        return new JScrollPane(tabelaDespesas);
    }

    // Método privado que configura comportamentos visuais internos da própria tela
    private void configurarEventosBasicos() {
        // Adiciona um ouvinte que detecta quando o usuário clica em alguma linha da tabela
        tabelaDespesas.getSelectionModel().addListSelectionListener(e -> {
            // Obtém o índice da linha clicada (-1 se nenhuma estiver selecionada)
            int linha = tabelaDespesas.getSelectedRow();
            // Verifica se uma linha válida foi selecionada
            if (linha >= 0) {
                // Copia o texto da coluna 1 (Descrição) para o campo txtDescricao
                txtDescricao.setText(tableModel.getValueAt(linha, 1).toString());
                // Seleciona no JComboBox o mesmo tipo que está na coluna 2 (Tipo)
                cbTipo.setSelectedItem(tableModel.getValueAt(linha, 2).toString());
                // Copia o valor da coluna 3 (Valor) para o campo txtValor
                txtValor.setText(tableModel.getValueAt(linha, 3).toString());
            }
        });}}

// Adiciona um evento ao botão Limpar para apagar os campos quando clicado
