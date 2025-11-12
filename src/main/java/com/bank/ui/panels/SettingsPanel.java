package com.bank.ui.panels;

import com.bank.controllers.SettingsPanelController;
import com.bank.models.Employee;
import com.bank.ui.Theme;
import com.bank.ui.components.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SettingsPanel extends JPanel {
    private final Map<String, JTextField> generalConfigs = new HashMap<>();
    private static final String[][] generalConfigLabels = new String[][] {
            {"outdoorQueueSize", "Outdoor Queue Size"},
            {"numOutdoorTellers", "Number of Outdoor Tellers"},
            {"numIndoorTellers", "Number of Indoor Tellers"},
            {"numIndoorServiceEmp", "Number of Indoor Service Employees"},
    };

    private final SettingsPanelController controller;

    private JPanel tablesPanel;
    private final Map<Employee, ProbabilitiesTable> employeeTables = new HashMap<>();

    public SettingsPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel content = new JPanel();
        content.setLayout(new GridBagLayout());
        content.setBackground(Theme.BACKGROUND);

        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.insets = new Insets(0, 0, 40, 0);
        c.anchor = GridBagConstraints.NORTH;
        content.add(prepareHeaderPanel(), c);

        c.insets = new Insets(0, 0, 40, 0);
        c.gridy = 1;
        content.add(prepareGeneralSettingsPanel(), c);

        c.insets = new Insets(0, 0, 40, 0);
        c.gridy = 2;
        content.add(prepareProbabilityDistributionPanel(), c);

        add(content, BorderLayout.NORTH);

        controller = new SettingsPanelController(this);
    }

    private JPanel prepareGeneralSettingsPanel() {
        ThemePanel panel = new ThemePanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(20, 20, 10, 20);

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        JLabel title = new JLabel("General Configuration");
        title.setFont(Theme.TITLE_FONT);
        panel.add(title, c);


        for (int i = 0; i < generalConfigLabels.length; i++) {
            int col = i % 3; // Column position (0, 1, 2)
            int rowPair = (i / 3) * 2; // Which pair of rows (0, 2, 4, 6...)

            boolean isFirstInRow = (col == 0);
            boolean isLastInRow = (col == 2) || (i == generalConfigLabels.length - 1);

            c.insets = new Insets(
                    10,
                    isFirstInRow ? 20 : 4,
                    0,
                    isLastInRow ? 20 : 4);

            c.gridy = rowPair + 1; // Label row
            c.gridx = col;

            JLabel label = new JLabel(generalConfigLabels[i][1]);
            label.setFont(Theme.DEFAULT_FONT);
            label.setForeground(Theme.TEXT_PRIMARY);
            panel.add(label, c);

            c.gridy = rowPair + 2; // Field row
            c.insets = new Insets(
                    4,
                    isFirstInRow ? 20 : 4,
                    0,
                    isLastInRow ? 20 : 4);

            if (i == generalConfigLabels.length - 1)
                c.insets = new Insets(
                        4,
                        isFirstInRow ? 20 : 4,
                        20,
                        isLastInRow ? 20 : 4
                );
            ThemeTextField field = new ThemeTextField(25);
            generalConfigs.put(generalConfigLabels[i][0], field);
            panel.add(field, c);
        }

        return panel;
    }

    private JPanel prepareHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setBackground(Theme.BACKGROUND);

        JPanel titleSection = new JPanel();
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleSection.setBackground(Theme.BACKGROUND);

        JLabel title = new JLabel("Simulation Settings");
        title.setFont(Theme.HEADER_FONT);
        headerPanel.add(title, BorderLayout.NORTH);

        titleSection.add(title);
        titleSection.add(Box.createVerticalStrut(10));

        JLabel subtitle = new JLabel("Configure the parameters for the simulation");
        subtitle.setFont(Theme.TITLE_FONT);
        subtitle.setForeground(Theme.TEXT_SECONDARY);
        titleSection.add(subtitle);

        headerPanel.add(titleSection);

        headerPanel.add(Box.createHorizontalGlue());

        ThemeButton resetBtn = new ThemeButton("Reset to Default", ThemeButton.Variant.DEFAULT);
        resetBtn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        headerPanel.add(resetBtn);

        headerPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        ThemeButton saveBtn = new ThemeButton("Save", ThemeButton.Variant.PRIMARY);
        saveBtn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        headerPanel.add(saveBtn);

        return headerPanel;
    }

    private JPanel prepareProbabilityDistributionPanel() {
        ThemePanel panel = new ThemePanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(20, 20, 10, 20);

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        JLabel title = new JLabel("Probability Distribution");
        title.setFont(Theme.TITLE_FONT);
        panel.add(title, c);

        c.gridy = 1;
        c.insets = new Insets(10, 20, 20, 20);
        c.weighty = 0;
        c.fill = GridBagConstraints.BOTH;

        tablesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.add(tablesPanel, c);

        return panel;
    }

    public void setGeneralConfigField(String key, String value) {
        JTextField field = generalConfigs.get(key);
        if (field != null) {
            field.setText(value);
        }
    }

    public String getGeneralConfigField(String key) {
        JTextField field = generalConfigs.get(key);
        if (field != null) {
            return field.getText();
        }
        return null;
    }

    public void addEmployee(Employee employee) {
        employeeTables.put(employee, new ProbabilitiesTable(employee));
        tablesPanel.add(employeeTables.get(employee));
    }

}
