package com.bank.ui.components;

import com.bank.models.Employee;
import com.bank.models.Range;
import com.bank.ui.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;

public class ProbabilitiesTable extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;

    public ProbabilitiesTable(Employee employee) {
        this();

        ArrayList<Range> probabilities = employee.getServiceTimeProbabilities();
        for(Range range : probabilities) {
            tableModel.addRow(new Object[]{range.low(), range.high(), ""});
        }
    }

    public ProbabilitiesTable() {
        setLayout(new BorderLayout(0, 10));
        setBackground(Theme.PANEL_BG);
//        setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        setPreferredSize(new Dimension(100, 400));
        setMinimumSize(new Dimension(100, 300));

        String[] columnNames = {"Value", "Probability", "Cumulative Probability"};

        // FIX: Make the table model editable with validation
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 2; // Only Value and Probability are editable, not Cumulative
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                if (column == 1) { // Probability column
                    try {
                        double value = Double.parseDouble(aValue.toString());
                        // Clamp between 0 and 1
                        if (value > 1.0) value = 1.0;
                        if (value < 0.0) value = 0.0;
                        super.setValueAt(value, row, column);
                        updateCumulativeProbabilities();
                        return;
                    } catch (NumberFormatException e) {
                        // Invalid input, ignore
                        return;
                    }
                }
                super.setValueAt(aValue, row, column);
            }
        };

        table = new JTable(tableModel);

        // --- Style the Table ---
        table.setRowHeight(40);
        table.setFont(Theme.DEFAULT_FONT.deriveFont(14f));
        table.setForeground(Theme.TEXT_PRIMARY);
        table.setGridColor(Theme.BORDER);
        table.setIntercellSpacing(new Dimension(1, 0)); // Add horizontal spacing
        table.setShowGrid(true); // Show grid lines
        table.setShowVerticalLines(true); // Show vertical lines between columns
        table.setShowHorizontalLines(false); // Hide horizontal lines
        table.setFillsViewportHeight(true);

        // Custom selection colors
        table.setSelectionBackground(Theme.PRIMARY_LIGHT);
        table.setSelectionForeground(Theme.PRIMARY);

        // Custom cell renderer for alternating row colors and padding
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                } else {
                    setBackground(row % 2 == 0 ? Theme.PANEL_BG : Theme.BACKGROUND);
                    // Make cumulative probability column look read-only
                    if (column == 2) {
                        setForeground(Theme.TEXT_SECONDARY);
                    } else {
                        setForeground(Theme.TEXT_PRIMARY);
                    }
                }
                setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
                return this;
            }
        };
        table.setDefaultRenderer(Object.class, cellRenderer);

        // --- Style the Header ---
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setFont(Theme.DEFAULT_FONT.deriveFont(Font.BOLD, 12f));
        header.setForeground(Theme.TEXT_SECONDARY);
        header.setBackground(Theme.BACKGROUND);
        header.setOpaque(false);

        // Custom header renderer for padding and border
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.BORDER),
                        BorderFactory.createEmptyBorder(10, 12, 10, 12)
                ));
                setHorizontalAlignment(SwingConstants.LEFT);
                setBackground(Theme.BACKGROUND);
                return this;
            }
        };
        header.setDefaultRenderer(headerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scrollPane.getViewport().setBackground(Theme.PANEL_BG);

        add(scrollPane, BorderLayout.CENTER);

        // --- Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Theme.BACKGROUND);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        ThemeButton addButton = new ThemeButton("Add Row", ThemeButton.Variant.PRIMARY);
        addButton.addActionListener(e -> addRow());

        ThemeButton removeButton = new ThemeButton("Remove Selected Row", ThemeButton.Variant.PRIMARY);
        removeButton.setBackgroundColor(Color.RED);
        removeButton.addActionListener(e -> removeSelectedRow());

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addRow() {
        tableModel.addRow(new Object[]{"0", "0.0", "0.0"});
        updateCumulativeProbabilities();
    }

    private void removeSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.removeRow(selectedRow);
            updateCumulativeProbabilities();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to remove.", "No Row Selected", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Update cumulative probabilities whenever probabilities change
    private void updateCumulativeProbabilities() {
        double cumulative = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object probValue = tableModel.getValueAt(i, 1);
            double prob = 0.0;
            try {
                prob = Double.parseDouble(probValue.toString());
            } catch (NumberFormatException e) {
                prob = 0.0;
            }
            cumulative += prob;
            tableModel.setValueAt(String.format("%.4f", cumulative), i, 2);
        }
    }

    // Helper method to get all table data
    public Object[][] getTableData() {
        int rowCount = tableModel.getRowCount();
        int colCount = tableModel.getColumnCount();
        Object[][] data = new Object[rowCount][colCount];

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                data[i][j] = tableModel.getValueAt(i, j);
            }
        }
        return data;
    }
}