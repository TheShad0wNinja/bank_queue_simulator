package com.bank.ui.pages;

import com.bank.ui.Theme;

import javax.swing.*;
import java.awt.*;

public class HistoryPanel extends JPanel {
    public HistoryPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("History");
        title.setFont(Theme.HEADER_FONT);

        JLabel placeholder = new JLabel("No past simulations found.");
        placeholder.setFont(Theme.DEFAULT_FONT);
        placeholder.setForeground(Theme.TEXT_SECONDARY);
        placeholder.setHorizontalAlignment(SwingConstants.CENTER);

        add(title, BorderLayout.NORTH);
        add(placeholder, BorderLayout.CENTER);
    }
}
