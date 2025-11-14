package com.bank.controllers;

import com.bank.models.EventPrinter;
import com.bank.simulation.Simulator;
import com.bank.ui.components.SimulationEventsTable;
import com.bank.ui.components.SimulationStatisticsTable;
import com.bank.ui.pages.SimulationPanel;

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimulationPanelController {
    private final SimulationPanel view;
    private final Simulator simulator;
    private Map<String, JTextField> simulationParamFields;
    private final SimulationEventsTable simulationEventsTable = new SimulationEventsTable();
    private final SimulationStatisticsTable firstDayStatsTable = new SimulationStatisticsTable();
    private final SimulationStatisticsTable firstBatchStatsTable = new SimulationStatisticsTable();
    private final SimulationStatisticsTable totalStatsTable = new SimulationStatisticsTable();

    public SimulationPanelController(SimulationPanel view) {
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
        view.setSimulationEventsTable(simulationEventsTable);
        view.setFirstDayResultsTable(firstDayStatsTable);
        view.setFirstBatchStats(firstBatchStatsTable);
        view.setTotalStats(totalStatsTable);
    }

    private void startSimulation() {
        view.clearSimulationResults();

        simulator.setSimulationCustomersCount(Integer.parseInt(simulationParamFields.get("simulation_customers").getText()));
        simulator.setSimulationDays(Integer.parseInt(simulationParamFields.get("simulation_days").getText()));
        simulator.setSimulationRetries(Integer.parseInt(simulationParamFields.get("simulation_repetition").getText()));

        simulator.startSimulation();
        firstDayStatsTable.setStatistics(simulator.getFirstDayStats().getStatistics());
        firstBatchStatsTable.setStatistics(simulator.getFirstBatchStats().getStatistics());
        totalStatsTable.setStatistics(simulator.getTotalStats().getStatistics());
        view.showResults();

        showSuccessMessage("Simulation Finished!");
    }

    private void setupActions() {
        view.setStartButtonAction(action -> startSimulation());
    }

    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(view, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
