package com.bank.controllers;

import com.bank.models.EventPrinter;
import com.bank.simulation.Simulator;
import com.bank.ui.components.SimulationEventsTable;
import com.bank.ui.components.SimulationStatisticsTable;
import com.bank.ui.pages.SimulationPage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimulationPageController {
    private final SimulationPage view;
    private final Simulator simulator;
    private Map<String, JTextField> simulationParamFields;
    private final SimulationEventsTable simulationEventsTable = new SimulationEventsTable();
    private final SimulationStatisticsTable firstDayStatsTable = new SimulationStatisticsTable();
    private final SimulationStatisticsTable firstBatchStatsTable = new SimulationStatisticsTable();
    private final SimulationStatisticsTable totalStatsTable = new SimulationStatisticsTable();

    public SimulationPageController(SimulationPage view) {
        this.view = view;
        this.simulator = new Simulator();
        this.simulator.addListener(new EventPrinter(simulationEventsTable));

        loadParams();
        setupActions();
    }

    private void loadParams() {
        simulationParamFields = view.addSimulationParameter(new LinkedHashMap<>(){{
            put("simulation_days", "Simulation Days");
            put("simulation_customers", "Customers per Day");
            put("simulation_repetition", "Simulation Repetition");
        }});
    }

    private void startSimulation() {
        view.clearSimulationResults();
        simulationEventsTable.clearEvents();

        simulator.setSimulationCustomersCount(Integer.parseInt(simulationParamFields.get("simulation_customers").getText()));
        simulator.setSimulationDays(Integer.parseInt(simulationParamFields.get("simulation_days").getText()));
        simulator.setSimulationRetries(Integer.parseInt(simulationParamFields.get("simulation_repetition").getText()));

        simulator.startSimulation();

        firstDayStatsTable.setStatistics(simulator.getFirstDayStats().getStatistics());
        firstBatchStatsTable.setStatistics(simulator.getFirstBatchStats().getStatistics());
        totalStatsTable.setStatistics(simulator.getTotalStats().getStatistics());

        view.addDataTable("First Run's Simulation Events", simulationEventsTable, 400);
        view.addDataTable("First Run Statistics", firstDayStatsTable, 300);
        view.addDataTable("First Batch Statistics", firstBatchStatsTable, 300);
        view.addDataTable("Total Statistics", totalStatsTable, 300);

        view.addChart("Average Service Times", createAvgServiceTimeChart());
        view.addChart("Average Wait Times", createAvgWaitTimesChart());
        view.addChart("Maximum Queue Sizes", createMaxQueueSizeChart());
        view.addChart("Wait Probability Distribution", createWaitProbabilityPieChart());
        view.addChart("Idle vs Busy Portion", createIdlePortionChart());

        view.showResults();
        showSuccessMessage("Simulation Finished!");
    }

    private JFreeChart createAvgServiceTimeChart() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();

        var stats = simulator.getTotalStats().getStatistics();
        for (var s : stats) {
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

        var stats = simulator.getTotalStats().getStatistics();
        for (var s : stats) {
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

        var stats = simulator.getTotalStats().getStatistics();
        for (var s : stats) {
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

        var stats = simulator.getTotalStats().getStatistics();
        for (var s : stats) {
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

        var stats = simulator.getTotalStats().getStatistics();
        for (var s : stats) {
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

    private void setupActions() {
        view.setStartButtonAction(action -> startSimulation());
    }

    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(view, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}