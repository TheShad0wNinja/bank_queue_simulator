package com.bank.ui.pages;

import com.bank.models.SimulationHistoryRecord;
import com.bank.ui.Theme;
import com.bank.ui.components.*;
import com.bank.utils.TextUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class HistoryDetailPage extends JPanel {
    private final SimulationHistoryRecord record;
    private JTabbedPane tabbedPane;

    public HistoryDetailPage(SimulationHistoryRecord record) {
        this.record = record;
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel header = prepareHeaderPanel();
        add(header, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Theme.PRIMARY_LIGHT);
        tabbedPane.setForeground(Theme.PRIMARY);
        tabbedPane.setFont(Theme.DEFAULT_FONT);

        tabbedPane.addTab("Configuration", prepareConfigTab());
        tabbedPane.addTab("Results", prepareResultsTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel prepareHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Theme.BACKGROUND);

        JLabel title = new JLabel("Simulation Details");
        title.setFont(Theme.HEADER_FONT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(title);

        headerPanel.add(Box.createVerticalStrut(5));

        String dateStr = record.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        JLabel dateLabel = new JLabel("Run Date: " + dateStr);
        dateLabel.setFont(Theme.DEFAULT_FONT);
        dateLabel.setForeground(Theme.TEXT_SECONDARY);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(dateLabel);

        headerPanel.add(Box.createVerticalStrut(40));

        return headerPanel;
    }

    private JScrollPane prepareConfigTab() {
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
        configPanel.setBackground(Theme.BACKGROUND);
        configPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel generalConfigTitle = new JLabel("General Configurations");
        generalConfigTitle.setFont(Theme.TITLE_FONT);
        generalConfigTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        configPanel.add(generalConfigTitle);
        configPanel.add(Box.createVerticalStrut(5));
        configPanel.add(prepareGeneralConfigPanel());
        configPanel.add(Box.createVerticalStrut(40));

        JLabel distributionsTitle = new JLabel("Distributions");
        distributionsTitle.setFont(Theme.TITLE_FONT);
        distributionsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        configPanel.add(distributionsTitle);
        configPanel.add(Box.createVerticalStrut(5));
        configPanel.add(prepareDistributionsPanel());

        JScrollPane scrollPane = new JScrollPane(configPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JPanel prepareGeneralConfigPanel() {
        ThemePanel panel = new ThemePanel();
        panel.setLayout(new GridLayout(0, 2, 20, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        SimulationHistoryRecord.SimulationConfigSnapshot config = record.getConfigSnapshot();
        SimulationHistoryRecord.SimulationParams params = record.getSimulationParams();

        String[][] configs = {
                {"Outdoor Queue Size", String.valueOf(config.getOutdoorQueueCapacity())},
                {"Probability of Cash Customer", String.valueOf(config.getCashCustomerProbability())},
                {"Number of Outdoor Tellers", String.valueOf(countEmployees(config.getEmployees(), "OUTDOOR", "CASH"))},
                {"Number of Indoor Tellers", String.valueOf(countEmployees(config.getEmployees(), "INDOOR", "CASH"))},
                {"Number of Indoor Service Employees", String.valueOf(countEmployees(config.getEmployees(), "INDOOR", "SERVICE"))},
                {"Simulation Days", String.valueOf(params.getSimulationDays())},
                {"Customers per Day", String.valueOf(params.getSimulationCustomers())},
                {"Simulation Repetition", String.valueOf(params.getSimulationRepetition())}
        };

        for (String[] configLabel : configs) {
            JPanel cell = new JPanel(new BorderLayout(5, 5));
            cell.setBackground(Theme.PANEL_BG);

            JLabel label = new JLabel(configLabel[0]);
            label.setFont(Theme.DEFAULT_FONT);
            label.setForeground(Theme.TEXT_PRIMARY);
            cell.add(label, BorderLayout.NORTH);

            ThemeTextField field = new ThemeTextField(25);
            field.setText(configLabel[1]);
            field.setEditable(false);
            cell.add(field, BorderLayout.CENTER);

            panel.add(cell);
        }

        return panel;
    }

    private int countEmployees(java.util.List<SimulationHistoryRecord.EmployeeConfigSnapshot> employees, String area, String type) {
        return (int) employees.stream()
                .filter(e -> e.getArea().equals(area) && e.getType().equals(type))
                .count();
    }

    private JPanel prepareDistributionsPanel() {
        ThemePanel panel = new ThemePanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel distributionsPanel = new JPanel();
        distributionsPanel.setLayout(new BoxLayout(distributionsPanel, BoxLayout.Y_AXIS));
        distributionsPanel.setBackground(Theme.PANEL_BG);

        SimulationHistoryRecord.SimulationConfigSnapshot config = record.getConfigSnapshot();

        ProbabilitiesTable timeBetweenArrivalsTable = new ProbabilitiesTable(config.getTimeBetweenArrivalProbabilities());
        timeBetweenArrivalsTable.setEnabled(false);
        addDistributionTable(distributionsPanel, "Time Between Arrivals", timeBetweenArrivalsTable);

        Map<String, java.util.List<SimulationHistoryRecord.EmployeeConfigSnapshot>> grouped = new LinkedHashMap<>();
        for (SimulationHistoryRecord.EmployeeConfigSnapshot emp : config.getEmployees()) {
            String key = emp.getArea() + "_" + emp.getType();
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(emp);
        }

        for (var entry : grouped.entrySet()) {
            for (int i = 0; i < entry.getValue().size(); i++) {
                SimulationHistoryRecord.EmployeeConfigSnapshot emp = entry.getValue().get(i);
                String label = TextUtils.capitalize(
                        String.join(" ",
                                Arrays.stream(entry.getKey().split("_"))
                                        .map(str -> str.matches("-?\\d+(\\.\\d+)?")
                                                ? String.valueOf(Integer.parseInt(str) + 1)
                                                : str)
                                        .toList())) + " " + (i + 1);

                ProbabilitiesTable table = new ProbabilitiesTable(emp.getServiceTimeProbabilities());
                table.setEnabled(false);
                addDistributionTable(distributionsPanel, label, table);
            }
        }

        panel.add(distributionsPanel, BorderLayout.CENTER);
        return panel;
    }

    private void addDistributionTable(JPanel parent, String title, ProbabilitiesTable table) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(Theme.PANEL_BG);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel label = new JLabel(title);
        label.setFont(Theme.DEFAULT_FONT.deriveFont(Font.BOLD, 16f));
        label.setForeground(Theme.TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.add(label);
        wrapper.add(Box.createVerticalStrut(10));

        table.setAlignmentX(Component.LEFT_ALIGNMENT);
        Dimension tablePref = table.getPreferredSize();
        table.setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.min(tablePref.height, 300)));
        wrapper.add(table);

        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, wrapper.getPreferredSize().height));
        parent.add(wrapper);
    }

    private JScrollPane prepareResultsTab() {
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(Theme.BACKGROUND);
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        SimulationEventsTable eventsTable = new SimulationEventsTable();
        for (SimulationHistoryRecord.EventRow event : record.getEvents()) {
            eventsTable.addEventRow(
                    event.time(),
                    event.type(),
                    event.customer(),
                    event.service(),
                    event.employee(),
                    event.queues(),
                    event.action()
            );
        }
        eventsTable.setEnabled(false);
        addDataTable(resultsPanel, "First Run's Simulation Events", eventsTable, 400);

        SimulationStatisticsTable firstRunStatsTable = new SimulationStatisticsTable();
        firstRunStatsTable.setStatistics(new ArrayList<>(record.getFirstRunStats()));
        firstRunStatsTable.setEnabled(false);
        addDataTable(resultsPanel, "First Run Statistics", firstRunStatsTable, 300);

        SimulationStatisticsTable firstBatchStatsTable = new SimulationStatisticsTable();
        firstBatchStatsTable.setStatistics(new ArrayList<>(record.getFirstBatchStats()));
        firstBatchStatsTable.setEnabled(false);
        addDataTable(resultsPanel, "First Batch Statistics", firstBatchStatsTable, 300);

        SimulationStatisticsTable totalStatsTable = new SimulationStatisticsTable();
        totalStatsTable.setStatistics(new ArrayList<>(record.getTotalStats()));
        totalStatsTable.setEnabled(false);
        addDataTable(resultsPanel, "Total Statistics", totalStatsTable, 300);

        addChart(resultsPanel, "Average Service Times", createAvgServiceTimeChart());
        addChart(resultsPanel, "Average Wait Times", createAvgWaitTimesChart());
        addChart(resultsPanel, "Maximum Queue Sizes", createMaxQueueSizeChart());
        addChart(resultsPanel, "Wait Probability Distribution", createWaitProbabilityPieChart());
        addChart(resultsPanel, "Idle vs Busy Portion", createIdlePortionChart());

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private void addDataTable(JPanel parent, String title, JPanel tablePanel, int height) {
        ThemePanel panel = new ThemePanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel(title);
        label.setFont(Theme.TITLE_FONT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(10));

        tablePanel.setPreferredSize(new Dimension(900, height));
        tablePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(tablePanel);
        parent.add(panel);
        parent.add(Box.createVerticalStrut(30));
    }

    private void addChart(JPanel parent, String title, JFreeChart chart) {
        ThemePanel panel = new ThemePanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel(title);
        label.setFont(Theme.TITLE_FONT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        org.jfree.chart.ChartPanel chartPanel = new org.jfree.chart.ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(900, 400));
        chartPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        chartPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(10));
        panel.add(chartPanel);
        parent.add(panel);
        parent.add(Box.createVerticalStrut(30));
    }

    private JFreeChart createAvgServiceTimeChart() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (var s : record.getTotalStats()) {
            String key = s.label();
            if (key.equals("Average Cash Customer Service Time") ||
                    key.equals("Average Service Customer Service Time")) {
                ds.addValue(parseStatValue(s.value()), "Avg Service Time", key);
            }
        }
        return ChartFactory.createBarChart(
                "Average Service Times",
                "Customer Type",
                "Avg Service Time",
                ds
        );
    }

    private JFreeChart createAvgWaitTimesChart() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (var s : record.getTotalStats()) {
            String key = s.label();
            if (key.equals("Average Indoor Teller Wait Time") ||
                    key.equals("Average Outdoor Teller Wait Time") ||
                    key.equals("Average Service Employee Wait Time") ||
                    key.equals("Average Total Wait Time")) {
                ds.addValue(parseStatValue(s.value()), "Avg Wait", key);
            }
        }
        return ChartFactory.createBarChart(
                "Average Wait Times",
                "Queue Type",
                "Avg Wait Time",
                ds
        );
    }

    private JFreeChart createMaxQueueSizeChart() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (var s : record.getTotalStats()) {
            String key = s.label();
            if (key.equals("Max Indoor Teller Queue Size") ||
                    key.equals("Max Outdoor Teller Queue Size") ||
                    key.equals("Max Service Employee Queue Size")) {
                ds.addValue(parseStatValue(s.value()), "Max Queue Size", key);
            }
        }
        return ChartFactory.createBarChart(
                "Maximum Queue Sizes",
                "Queue",
                "Max Size",
                ds
        );
    }

    private JFreeChart createIdlePortionChart() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (var s : record.getTotalStats()) {
            String key = s.label();
            if (key.endsWith("Idle Portion")) {
                double idle = parseStatValue(s.value());
                double busy = 100.0 - idle;
                ds.addValue(idle, "Idle %", key);
                ds.addValue(busy, "Busy %", key);
            }
        }
        return ChartFactory.createStackedBarChart(
                "Idle vs Busy Time",
                "Employee",
                "Percentage",
                ds
        );
    }

    private JFreeChart createWaitProbabilityPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (var s : record.getTotalStats()) {
            if (s.label().toLowerCase().contains("wait probability")) {
                double val = parseStatValue(s.value());
                dataset.setValue(s.label(), val);
            }
        }
        return ChartFactory.createPieChart(
                "Wait Probability Distribution",
                dataset,
                true, true, false
        );
    }

    private double parseStatValue(String value) {
        if (value == null) return 0;
        try {
            if (value.endsWith("%")) {
                return Double.parseDouble(value.replace("%", "").trim());
            }
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

