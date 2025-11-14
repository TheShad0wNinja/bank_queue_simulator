package com.bank.ui.pages;

import com.bank.controllers.SimulationPanelController;
import com.bank.ui.Theme;
import com.bank.ui.components.SimulationEventsTable;
import com.bank.ui.components.ThemeButton;
import com.bank.ui.components.ThemePanel;
import com.bank.ui.components.ThemeTextField;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SimulationPanel extends JPanel {
    private JPanel simulationParamsPanel;
    private JButton startSimulationButton;
    private JPanel simulationResultsPanel;
    private SimulationEventsTable simulationEventsTable;

    public SimulationPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Simulation");
        header.setFont(Theme.HEADER_FONT);

        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new GridBagLayout());
        content.setBackground(Theme.BACKGROUND);

        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(30, 0, 5, 0);
        JLabel title = new JLabel("Simulation Parameters");
        title.setFont(Theme.TITLE_FONT);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        content.add(title, c);

        c.gridy++;
        c.insets = new Insets(5, 0, 0, 0);
        content.add(prepareSimulationParametersPanel(), c);

        c.gridy++;
        c.insets = new Insets(20, 0, 0, 0);
        content.add(prepareSimulationStartButton(), c);

        c.gridy++;
        c.insets = new Insets(30, 0, 5, 0);
        JLabel simResultsTitle = new JLabel("Simulation Results");
        simResultsTitle.setFont(Theme.TITLE_FONT);
        simResultsTitle.setHorizontalAlignment(SwingConstants.LEFT);
        content.add(simResultsTitle, c);

        c.gridy++;
        c.insets = new Insets(5, 0, 0, 0);
        content.add(prepareSimulationResultsPanel(), c);

        c.gridy++;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        content.add(Box.createVerticalGlue(), c);

        add(content, BorderLayout.CENTER);

        new SimulationPanelController(this);
    }

    private JButton prepareSimulationStartButton() {
        startSimulationButton = new ThemeButton("Start Simulation", ThemeButton.Variant.PRIMARY);

        return startSimulationButton;
    }

    private JPanel prepareSimulationParametersPanel() {
        ThemePanel panel = new ThemePanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        simulationParamsPanel = new JPanel();
        simulationParamsPanel.setLayout(new GridBagLayout());
        simulationParamsPanel.setBackground(Theme.PANEL_BG);

        panel.add(simulationParamsPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel prepareSimulationResultsPanel() {
        simulationResultsPanel = new ThemePanel();
        simulationResultsPanel.setLayout(new GridBagLayout());
        simulationResultsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        simulationResultsPanel.setVisible(false);

        return simulationResultsPanel;
    }

    public void setSimulationEventsTable(SimulationEventsTable simulationEventsTable) {
        this.simulationEventsTable = simulationEventsTable;

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 0);
        JLabel title = new JLabel("First Run's Simulation Events");
        title.setFont(Theme.TITLE_FONT);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        simulationParamsPanel.add(title, c);

        c.gridy++;
        c.insets = new Insets(5, 0, 0, 0);
        simulationResultsPanel.add(simulationEventsTable, c);
    }

    public void clearSimulationEventsTable() {
        simulationEventsTable.clearEvents();
    }

    public Map<String, JTextField> addSimulationParameter(Map<String, String> parameters) {
        simulationParamsPanel.removeAll();

        Map<String, JTextField> map = new HashMap<>();

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 20, 0, 20);

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weightx = 1.0;
        int columns = 2;
        int i = 0;
        for (var entry : parameters.entrySet()) {
            int col = i % columns;
            int rowPair = (i / columns) * 2;

            c.gridx = col;
            c.gridy = rowPair + 1;
            c.insets = new Insets(4, col == 0 ? 20 : 8, 4, col == columns - 1 ? 20 : 8);

            JLabel label = new JLabel(entry.getValue());
            label.setFont(Theme.DEFAULT_FONT);
            label.setForeground(Theme.TEXT_PRIMARY);
            simulationParamsPanel.add(label, c);

            c.gridy = rowPair + 2;
            ThemeTextField field = new ThemeTextField(25);
            field.setText("10");
            map.put(entry.getKey(), field);
            simulationParamsPanel.add(field, c);
            i++;
        }

        c.gridx = 0;
        c.gridy += 2;
        c.gridwidth = 2;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        simulationParamsPanel.add(Box.createVerticalGlue(), c);

        return map;
    }

    public void setStartButtonAction(java.awt.event.ActionListener action) {
        for (var listener : startSimulationButton.getActionListeners()) {
            startSimulationButton.removeActionListener(listener);
        }
        startSimulationButton.addActionListener(action);
    }

    public void showResults() {
        simulationResultsPanel.setVisible(true);
    }

}
