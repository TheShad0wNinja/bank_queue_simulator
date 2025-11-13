package com.bank.ui.pages;

import com.bank.controllers.SettingsPanelController;
import com.bank.models.EmployeeData;
import com.bank.ui.Theme;
import com.bank.ui.components.*;
import com.bank.utils.TextUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SettingsPanel extends JPanel {
    private final Map<String, JTextField> generalConfigs = new HashMap<>();
    private static final String[][] generalConfigLabels = new String[][]{
            {"outdoorQueueSize", "Outdoor Queue Size"},
            {"cashCustomerProp", "Probability of Cash Customer"},
            {"numOutdoorTellers", "Number of Outdoor Tellers"},
            {"numIndoorTellers", "Number of Indoor Tellers"},
            {"numIndoorServiceEmp", "Number of Indoor Service Employees"},
    };

    private JPanel tablesPanel;
    private final Map<String, ProbabilitiesTable> employeeTables = new HashMap<>();
    private ProbabilitiesTable timeBetweenArrivalsTable;

    private JButton saveBtn;
    private JButton resetBtn;

    public SettingsPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);

        JPanel content = new JPanel();
        content.setLayout(new GridBagLayout());
        content.setBackground(Theme.BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.insets = new Insets(0, 0, 0, 0);
        c.anchor = GridBagConstraints.NORTH;
        content.add(prepareHeaderPanel(), c);


        c.gridy++;
        c.insets = new Insets(40, 0, 0, 0);
        JLabel generalConfigTitle = new JLabel("General Configurations");
        generalConfigTitle.setFont(Theme.TITLE_FONT);
        generalConfigTitle.setHorizontalAlignment(SwingConstants.LEFT);
        content.add(generalConfigTitle, c);

        c.insets = new Insets(5, 0, 0, 0);
        c.gridy++;
        content.add(prepareGeneralSettingsPanel(), c);


        c.gridy++;
        c.insets = new Insets(40, 0, 0, 0);
        JLabel title = new JLabel("Distributions");
        title.setFont(Theme.TITLE_FONT);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        content.add(title, c);

        c.insets = new Insets(5, 0, 0, 0);
        c.gridy++;
        content.add(prepareProbabilityDistributionPanel(), c);

        c.gridy++;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        content.add(Box.createVerticalGlue(), c);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        new SettingsPanelController(this);
    }

    private JPanel prepareGeneralSettingsPanel() {
        ThemePanel panel = new ThemePanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 20, 0, 20);

        c.gridy = 0;
        c.weightx = 1.0;

        c.gridwidth = 1;
        final int columns = 2;
        for (int i = 0; i < generalConfigLabels.length; i++) {
            int col = i % columns;
            int rowPair = (i / columns) * 2;

            c.gridx = col;
            c.gridy = rowPair + 1;
            c.insets = new Insets(10, col == 0 ? 20 : 8, 4, col == columns - 1 ? 20 : 8);

            JLabel label = new JLabel(generalConfigLabels[i][1]);
            label.setFont(Theme.DEFAULT_FONT);
            label.setForeground(Theme.TEXT_PRIMARY);
            panel.add(label, c);

            c.gridy = rowPair + 2;
            ThemeTextField field = new ThemeTextField(25);
            generalConfigs.put(generalConfigLabels[i][0], field);
            panel.add(field, c);
        }

        c.gridx = 0;
        c.gridy += 2;
        c.gridwidth = 2;
        c.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), c);
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

        titleSection.add(title);
        titleSection.add(Box.createVerticalStrut(10));

        JLabel subtitle = new JLabel("Configure the parameters for the simulation");
        subtitle.setFont(Theme.TITLE_FONT);
        subtitle.setForeground(Theme.TEXT_SECONDARY);
        titleSection.add(subtitle);

        headerPanel.add(titleSection);
        headerPanel.add(Box.createHorizontalGlue());

        resetBtn = new ThemeButton("Reset to Default", ThemeButton.Variant.DEFAULT);
        resetBtn.setFont(Theme.DEFAULT_FONT.deriveFont(Font.PLAIN, 18));
        headerPanel.add(resetBtn);

        headerPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        saveBtn = new ThemeButton("Save", ThemeButton.Variant.PRIMARY);
        saveBtn.setFont(Theme.DEFAULT_FONT.deriveFont(Font.PLAIN, 18));
        headerPanel.add(saveBtn);

        return headerPanel;
    }

    private JPanel prepareProbabilityDistributionPanel() {
        ThemePanel panel = new ThemePanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();


        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(20, 20, 20, 20);
        c.weighty = 1.0;

        tablesPanel = new JPanel();
        tablesPanel.setLayout(new BoxLayout(tablesPanel, BoxLayout.Y_AXIS));
        panel.add(tablesPanel, c);

        return panel;
    }

    // Public methods for controller interaction
    public void setGeneralConfigField(String key, String value) {
        JTextField field = generalConfigs.get(key);
        if (field != null) {
            field.setText(value);
        }
    }

    public String getGeneralConfigField(String key) {
        JTextField field = generalConfigs.get(key);
        return field != null ? field.getText() : "";
    }

    public void clearTables() {
        employeeTables.clear();
        timeBetweenArrivalsTable = null;
        tablesPanel.removeAll();
        tablesPanel.revalidate();
        tablesPanel.repaint();
    }

    public void setTimeBetweenArrivalsTable(Map<Integer, Double> probabilities) {
        timeBetweenArrivalsTable = addProbabilityTable("Time Between Arrivals", probabilities);
    }

    private ProbabilitiesTable addProbabilityTable(String labelText, Map<Integer, Double> probabilities) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Theme.PANEL_BG);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 700));

        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setBackground(Theme.PANEL_BG);
        labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel label = new JLabel(labelText);
        label.setFont(Theme.DEFAULT_FONT.deriveFont(Font.BOLD, 16f));
        label.setForeground(Theme.TEXT_PRIMARY);
        labelPanel.add(label, BorderLayout.WEST);

        wrapper.add(labelPanel, BorderLayout.NORTH);

        ProbabilitiesTable table = new ProbabilitiesTable(probabilities);
        wrapper.add(table, BorderLayout.CENTER);

        tablesPanel.add(wrapper);
        tablesPanel.revalidate();

        return table;
    }

    public void addEmployeeTable(String employeeKey, EmployeeData employeeData) {
        if (!employeeTables.containsKey(employeeKey)) {
            String employeeLabel = TextUtils.capitalize(
                    String.join(" ",
                            Arrays.stream(employeeKey.split("_"))
                                    .map(str ->
                                            str.matches("-?\\d+(\\.\\d+)?")
                                                    ? String.valueOf(Integer.parseInt(str) + 1)
                                                    : str
                                    ).toList()));

            employeeTables.put(employeeKey, addProbabilityTable(employeeLabel, employeeData.getServiceTimeProbabilities()));
        }

        tablesPanel.revalidate();
        tablesPanel.repaint();
    }

    public ProbabilitiesTable getTimeBetweenArrivalsTable() {
        return timeBetweenArrivalsTable;
    }

    public Map<String, ProbabilitiesTable> getAllEmployeeTables() {
        return new HashMap<>(employeeTables);
    }

    public void setSaveButtonAction(java.awt.event.ActionListener action) {
        for (var listener : saveBtn.getActionListeners()) {
            saveBtn.removeActionListener(listener);
        }
        saveBtn.addActionListener(action);
    }

    public void setResetButtonAction(java.awt.event.ActionListener action) {
        for (var listener : resetBtn.getActionListeners()) {
            resetBtn.removeActionListener(listener);
        }
        resetBtn.addActionListener(action);
    }
}