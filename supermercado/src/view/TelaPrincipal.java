package view;

import dao.GenericDao;
import model.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Set;

public class TelaPrincipal extends JFrame {
    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel model;
    private JTextField codigoField;
    private JTextField descricaoField;
    private JTextField precoField;
    private GenericDao<Produto> dao;

    public TelaPrincipal() {
        setTitle("Cadastro de Produtos");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // painel principal
        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        // inicializa DAO (arquivo produtos.dat na raiz do projeto)
        try {
            dao = new GenericDao<>("produtos.dat");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível criar ou abrir o arquivo de dados.\n" + ex.getMessage(),
                    "Erro de I/O",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // modelo e tabela
        model = new DefaultTableModel(new Object[]{"Código", "Descrição", "Preço"}, 0);
        table = new JTable(model);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);

        // painel de entrada
        codigoField    = new JTextField(5);
        descricaoField = new JTextField(15);
        precoField     = new JTextField(7);
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Código:"));
        inputPanel.add(codigoField);
        inputPanel.add(new JLabel("Descrição:"));
        inputPanel.add(descricaoField);
        inputPanel.add(new JLabel("Preço:"));
        inputPanel.add(precoField);
        contentPane.add(inputPanel, BorderLayout.NORTH);

        // botões CRUD + Sair
        JButton addButton    = new JButton("Adicionar");
        JButton updateButton = new JButton("Atualizar");
        JButton deleteButton = new JButton("Excluir");
        JButton sairButton   = new JButton("Sair");

        addButton.addActionListener(e -> onAdicionar());
        updateButton.addActionListener(e -> onAtualizar());
        deleteButton.addActionListener(e -> onExcluir());
        sairButton.addActionListener(e -> {
            System.out.println("Você saiu do cadastro");
            System.exit(0);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(sairButton);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        // seleção de linha preenche campos
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    codigoField.setText(model.getValueAt(row, 0).toString());
                    descricaoField.setText(model.getValueAt(row, 1).toString());
                    precoField.setText(model.getValueAt(row, 2).toString());
                }
            }
        });

        // carrega produtos já salvos
        loadProdutos();
    }

    private void loadProdutos() {
        try {
            Set<Produto> produtos = dao.getAll();
            for (Produto p : produtos) {
                model.addRow(new Object[]{
                        p.getCodigo(),
                        p.getDescricao(),
                        String.format("%.2f", p.getPreco())
                });
            }
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar produtos:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAdicionar() {
        try {
            double preco = Double.parseDouble(precoField.getText());
            Produto p = new Produto(
                    codigoField.getText(),
                    descricaoField.getText(),
                    preco
            );
            if (dao.salvar(p)) {
                model.addRow(new Object[]{
                        p.getCodigo(),
                        p.getDescricao(),
                        String.format("%.2f", p.getPreco())
                });
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Já existe um produto igual.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Preço inválido. Use ponto para decimais.",
                    "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this,
                    "Falha ao salvar:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAtualizar() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        String oldCodigo    = model.getValueAt(row, 0).toString();
        String oldDesc      = model.getValueAt(row, 1).toString();
        double oldPreco     = Double.parseDouble(model.getValueAt(row, 2).toString());

        try {
            double novoPreco = Double.parseDouble(precoField.getText());
            Produto oldP = new Produto(oldCodigo, oldDesc, oldPreco);
            Produto newP = new Produto(
                    codigoField.getText(),
                    descricaoField.getText(),
                    novoPreco
            );

            if (dao.remover(oldP) && dao.salvar(newP)) {
                model.setValueAt(newP.getCodigo(), row, 0);
                model.setValueAt(newP.getDescricao(), row, 1);
                model.setValueAt(String.format("%.2f", newP.getPreco()), row, 2);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Falha ao atualizar o produto.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Preço inválido.",
                    "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro I/O:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onExcluir() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        String codigo    = model.getValueAt(row, 0).toString();
        String descricao = model.getValueAt(row, 1).toString();
        double preco     = Double.parseDouble(model.getValueAt(row, 2).toString());
        Produto p = new Produto(codigo, descricao, preco);

        try {
            if (dao.remover(p)) {
                model.removeRow(row);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Não foi possível remover o produto.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this,
                    "Falha I/O:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        codigoField.setText("");
        descricaoField.setText("");
        precoField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new TelaPrincipal().setVisible(true)
        );
    }
}