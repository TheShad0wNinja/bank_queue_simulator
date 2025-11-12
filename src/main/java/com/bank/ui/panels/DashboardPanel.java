package com.bank.ui.panels;

import com.bank.ui.Theme;
import com.bank.ui.components.*;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Simulation Results");
        header.setFont(Theme.HEADER_FONT);

        ThemePanel card = new ThemePanel();
        card.setLayout(new GridLayout(2, 1));
        JLabel value = new JLabel("3.1");
        value.setFont(Theme.HEADER_FONT);
        card.add(value);
        card.setPreferredSize(new Dimension(250, 100));

        JPanel grid = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        grid.setOpaque(false);
        grid.add(card);

        add(header, BorderLayout.NORTH);
        add(grid, BorderLayout.CENTER);
    }
}
