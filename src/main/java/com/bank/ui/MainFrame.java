package com.bank.ui;

import com.bank.ui.components.*;
import com.bank.ui.pages.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class MainFrame extends JFrame {

    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private final HashMap<String, JPanel> panels = new HashMap<>();
    private final String defaultPage = "simulation";

    public MainFrame() {
        setTitle("BankQueue Sim");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BACKGROUND);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Theme.BACKGROUND);

        SimulationPanel simulationPanel = new SimulationPanel();
        HistoryPanel historyPanel = new HistoryPanel();
        SettingsPanel settingsPanel = new SettingsPanel();

        panels.put("simulation", simulationPanel);
        panels.put("history", historyPanel);
        panels.put("settings", settingsPanel);

        contentPanel.add(simulationPanel, "simulation");
        contentPanel.add(historyPanel, "history");
        contentPanel.add(settingsPanel, "settings");
        add(contentPanel, BorderLayout.CENTER);

        showPanel(defaultPage);

        MainSideNav sideNav = new MainSideNav(new String[]{"simulation", "history", "settings"}, defaultPage);
        sideNav.setOnSelect(this::showPanel);

        add(sideNav, BorderLayout.WEST);
    }

    private void showPanel(String name) {
        cardLayout.show(contentPanel, name);
    }
}
