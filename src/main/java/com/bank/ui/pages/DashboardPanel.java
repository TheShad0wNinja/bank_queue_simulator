package com.bank.ui.pages;

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

        add(header, BorderLayout.NORTH);
    }
}
