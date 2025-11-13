package com.bank.ui.components;

import com.bank.ui.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class SimulationEventsTable extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;

    public SimulationEventsTable() {
        setLayout(new BorderLayout(0, 10));
        setBackground(Theme.PANEL_BG);

        String[] columnNames = {
                "Time",
                "Type",
                "Customer",
                "Service",
                "Employee",
                "Queues (O|I|S)",
                "Action"
        };

        // READ ONLY MODEL
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);

        // Table Appearance
        table.setFont(Theme.DEFAULT_FONT.deriveFont(13f));
        table.setRowHeight(32);
        table.setForeground(Theme.TEXT_PRIMARY);
        table.setGridColor(Theme.BORDER);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 0));

        // Alternating row colors
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column
            ) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    setBackground(Theme.PRIMARY_LIGHT);
                    setForeground(Theme.PRIMARY);
                } else {
                    setBackground(row % 2 == 0 ? Theme.PANEL_BG : Theme.BACKGROUND);
                    setForeground(Theme.TEXT_PRIMARY);
                }

                setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
                return this;
            }
        };
        table.setDefaultRenderer(Object.class, cellRenderer);

        // Header style
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setFont(Theme.DEFAULT_FONT.deriveFont(Font.BOLD, 12f));
        header.setForeground(Theme.TEXT_SECONDARY);
        header.setBackground(Theme.BACKGROUND);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column
            ) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.BORDER),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
                setHorizontalAlignment(SwingConstants.LEFT);

                return this;
            }
        };
        header.setDefaultRenderer(headerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scrollPane.getViewport().setBackground(Theme.PANEL_BG);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Append a simulation event as a new table row.
     */
    public void addEventRow(
            int time,
            String type,
            String customer,
            String service,
            String employee,
            String queues,
            String action
    ) {
        tableModel.addRow(new Object[]{
                time,
                type,
                customer,
                service,
                employee,
                queues,
                action
        });

        // Auto-scroll to bottom
        JScrollBar bar = ((JScrollPane) getComponent(0)).getVerticalScrollBar();
        bar.setValue(bar.getMaximum());
    }

    /**
     * Clear all events (useful when starting a new simulation)
     */
    public void clearEvents() {
        tableModel.setRowCount(0);
    }
}
