// Define que esta classe pertence ao pacote 'telas' (camada visual do padrão MVC)
package com.mycompany.cabenobolso.telas;

// Importa todas as classes do pacote modelo (Planejamento, Despesa, DespesaFixa, DespesaPorPessoa, Participante)
import com.mycompany.cabenobolso.modelo.*;
// Importa todos os componentes visuais do Swing (JPanel, JButton, JTextField, JTable, etc.)
import javax.swing.*;
// Importa o modelo padrão de dados usado para controlar as linhas e colunas da JTable
import javax.swing.table.DefaultTableModel;
// Importa classes de layout e dimensões gráficas (BorderLayout, GridBagLayout, Insets, Dimension)
import java.awt.*;
// Importa coleções para manipular listas e conjuntos de códigos de participantes
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

// Referência ao planejamento compartilhado por todo o sistema
public class TelaDespesas extends JPanel {

private Planejamento planejamento;

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
    // Construtor da classe: recebe o mesmo Planejamento usado nas outras telas do sistema
    public TelaDespesas(Planejamento planejamento) {
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
    // Método privado que conecta os cliques dos botões e da tabela às suas respectivas ações
    private void configurarEventos() {
        // Chama o método incluir() quando o botão "Incluir" for clicado
        btnIncluir.addActionListener(e -> incluir());
        // Chama o método editar() quando o botão "Salvar Edição" for clicado
        btnEditar.addActionListener(e -> editar());
        // Chama o método excluir() quando o botão "Excluir" for clicado
        btnExcluir.addActionListener(e -> excluir());
        // Chama o método limparCampos() quando o botão "Limpar" for clicado
        btnLimpar.addActionListener(e -> limparCampos());
        // Adiciona um ouvinte para detectar quando o usuário seleciona uma linha na tabela
        tabelaDespesas.getSelectionModel().addListSelectionListener(e -> {
            // Evita executar duas vezes durante o clique e preenche o formulário com a linha selecionada
            if (!e.getValueIsAdjusting()) carregarLinhaSelecionada();
        });
    }

    // Método privado responsável por cadastrar uma nova despesa no planejamento
    private void incluir() {
        // Lê e valida os dados digitados no formulário, montando um objeto Despesa
        Despesa d = lerFormulario();
        // Se algum dado estiver inválido (retornou null), interrompe a inclusão
        if (d == null) return;
        // Adiciona a nova despesa dentro do objeto Planejamento compartilhado
        planejamento.incluirDespesa(d);
        // Atualiza as linhas da tabela visual para exibir a despesa recém-cadastrada
        atualizarTabela();
        // Limpa os campos do formulário para um próximo cadastro
        limparCampos();
    }

    // Método privado responsável por salvar as alterações de uma despesa já existente
    private void editar() {
        // Obtém o índice da linha atualmente selecionada na tabela (-1 se nenhuma estiver marcada)
        int linha = tabelaDespesas.getSelectedRow();
        // Se nenhuma linha estiver selecionada, exibe um aviso e cancela a edição
        if (linha < 0) { aviso("Selecione uma despesa na tabela."); return; }
        // Lê e valida os novos dados preenchidos no formulário
        Despesa nova = lerFormulario();
        // Se houver erro de validação, interrompe sem alterar a despesa antiga
        if (nova == null) return;
        // Substitui a despesa na posição 'linha' dentro do Planejamento
        planejamento.editarDespesa(linha, nova);
        // Atualiza a tabela visual com os dados modificados
        atualizarTabela();
        // Limpa os campos e desmarca a seleção da tabela
        limparCampos();
    }

    // Método privado responsável por remover a despesa selecionada na tabela
    private void excluir() {
        // Obtém o índice da linha selecionada na tabela
        int linha = tabelaDespesas.getSelectedRow();
        // Se nenhuma despesa estiver selecionada, avisa o usuário e interrompe
        if (linha < 0) { aviso("Selecione uma despesa para excluir."); return; }
        // Remove a despesa correspondente do Planejamento
        planejamento.excluirDespesa(linha);
        // Atualiza a tabela visual removendo a linha excluída
        atualizarTabela();
        // Limpa os campos do formulário
        limparCampos();
    }
           // Método público que carrega os participantes do planejamento na lista visual (JList)
    public void atualizarParticipantes() {
        // Remove todos os itens atuais da lista para evitar duplicação
        listModelParticipantes.clear();
        // Percorre cada participante cadastrado no objeto Planejamento
        for (Participante p : planejamento.getParticipantes()) {
            // Adiciona no formato "código - nome" (ex: "1 - Maria") para facilitar a identificação
            listModelParticipantes.addElement(p.getCodigo() + " - " + p.getNome());
        }
    }

    // Método público que recarrega todas as despesas do planejamento na tabela visual
    public void atualizarTabela() {
        // Apaga todas as linhas atuais da tabela antes de preencher novamente
        tableModel.setRowCount(0);
        // Obtém a lista atualizada de despesas armazenadas no Planejamento
        List<Despesa> lista = planejamento.getDespesas();
        // Percorre a lista de despesas pelo índice
        for (int i = 0; i < lista.size(); i++) {
            // Pega a despesa na posição atual
            Despesa d = lista.get(i);
            // Verifica a subclasse da despesa para exibir "Fixa" ou "Por pessoa" na coluna Tipo
            String tipo = (d instanceof DespesaFixa) ? "Fixa" : "Por pessoa";
            // Converte o valor armazenado em centavos (long) para reais formatados com vírgula
            String valor = String.format("%.2f", d.getValorCentavos() / 100.0).replace(".", ",");
            // Adiciona uma nova linha na tabela com ID sequencial, descrição, tipo, valor e códigos dos participantes
            tableModel.addRow(new Object[]{i + 1, d.getDescricao(), tipo, valor, d.getParticipantes()});
        }
    }

    // Método privado que valida os campos do formulário e cria o objeto Despesa correspondente
    private Despesa lerFormulario() {
        // Captura o texto da descrição removendo espaços extras nas pontas
        String desc = txtDescricao.getText().trim();
        // Valida se a descrição está vazia; se estiver, avisa e retorna null
        if (desc.isEmpty()) { aviso("Informe a descrição."); return null; }
        // Variável que guardará o valor convertido em centavos
        long valor;
        try {
            // Troca vírgula por ponto, converte para double, multiplica por 100 e arredonda para centavos
            valor = Math.round(Double.parseDouble(txtValor.getText().replace(",", ".")) * 100);
        } catch (Exception e) {
            // Caso o usuário digite letras ou formato inválido, exibe aviso e interrompe
            aviso("Valor inválido."); return null;
        }
        // Impede valores negativos ou iguais a zero
        if (valor <= 0) { aviso("O valor deve ser maior que zero."); return null; }
        // Cria um conjunto ordenado (TreeSet) para armazenar os códigos dos participantes selecionados
        Set<Integer> cods = new TreeSet<>();
        // Percorre cada item selecionado pelo usuário na lista visual de participantes
        for (String s : listParticipantes.getSelectedValuesList()) {
            // Separa o texto "código - nome" pelo hífen e converte a primeira parte (código) para inteiro
            cods.add(Integer.parseInt(s.split(" - ")[0]));
        }
        // Valida se pelo menos um participante foi selecionado
        if (cods.isEmpty()) { aviso("Selecione ao menos um participante."); return null; }
        // Instancia DespesaFixa (índice 0) ou DespesaPorPessoa (índice 1) conforme escolhido no JComboBox
        return cbTipo.getSelectedIndex() == 0 ? new DespesaFixa(desc, valor, cods) : new DespesaPorPessoa(desc, valor, cods);
    }
    // Método privado que preenche o formulário quando o usuário clica em uma linha da tabela
    private void carregarLinhaSelecionada() {
        // Obtém o índice da linha selecionada na tabela
        int linha = tabelaDespesas.getSelectedRow();
        // Se nenhuma linha estiver selecionada, não faz nada
        if (linha < 0) return;
        // Busca o objeto Despesa correspondente no Planejamento
        Despesa d = planejamento.getDespesas().get(linha);
        // Preenche o campo de descrição com o nome da despesa
        txtDescricao.setText(d.getDescricao());
        // Seleciona "Fixa" (0) ou "Por pessoa" (1) no menu suspenso conforme o tipo da despesa
        cbTipo.setSelectedIndex(d instanceof DespesaFixa ? 0 : 1);
        // Converte os centavos para reais com vírgula e exibe no campo de valor
        txtValor.setText(String.format("%.2f", d.getValorCentavos() / 100.0).replace(".", ","));
        // Limpa seleções anteriores na lista de participantes antes de marcar os atuais
        listParticipantes.clearSelection();
        // Percorre todos os participantes exibidos na lista visual
        for (int i = 0; i < listModelParticipantes.size(); i++) {
            // Extrai o código numérico do participante da linha atual da lista
            int cod = Integer.parseInt(listModelParticipantes.get(i).split(" - ")[0]);
            // Se esse código estiver associado à despesa, seleciona o participante na lista
            if (d.getParticipantes().contains(cod)) listParticipantes.addSelectionInterval(i, i);
        }
    }

    // Método privado que limpa todos os campos do formulário e desmarca a tabela
    private void limparCampos() {
        // Remove a seleção de linha na tabela de despesas
        tabelaDespesas.clearSelection();
        // Apaga os textos digitados nos campos descrição e valor
        txtDescricao.setText(""); txtValor.setText("");
        // Volta o tipo selecionado para a primeira opção ("Fixa")
        cbTipo.setSelectedIndex(0);
        // Desmarca todos os participantes selecionados na lista
        listParticipantes.clearSelection();
    }

    // Método auxiliar privado para exibir janelas de alerta padrão ao usuário
    private void aviso(String msg) { JOptionPane.showMessageDialog(this, msg, "Atenção", JOptionPane.WARNING_MESSAGE); }
// Fecha a declaração da classe TelaDespesas
}
