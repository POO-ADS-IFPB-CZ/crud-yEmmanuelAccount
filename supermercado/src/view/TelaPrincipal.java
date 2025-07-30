package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TelaPrincipal extends JFrame {
    private JPanel contentPane;
    private JTable tabela;
    private DefaultTableModel tableModel;
    private JButton btnAdicionar;
    private JButton btnAtualizar;
    private JButton btnRemover;

    private JButton buttonOK;

    public TelaPrincipal() {
        criarComponentes();
        configurarJanela();

        setContentPane(contentPane);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    private void criarComponentes() {
        // Painel raiz
        contentPane = new JPanel(new BorderLayout(10, 10));
        setContentPane(contentPane);

        // Modelo e tabela
        tableModel = new DefaultTableModel(new Object[] {
                "Código", "Descrição", "Preço"
        }, 0);
        tabela = new JTable(tableModel);
        contentPane.add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Painel de botões na parte inferior
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnAdicionar = new JButton("Adicionar");
        btnAtualizar = new JButton("Atualizar");
        btnRemover   = new JButton("Remover");

        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnRemover);

        contentPane.add(painelBotoes, BorderLayout.SOUTH);
    }

    private void configurarJanela() {
        setTitle("Cadastro de Produtos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TelaPrincipal().setVisible(true);
        });
    }
}