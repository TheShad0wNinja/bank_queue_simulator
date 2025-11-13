package com.bank.controllers;

import com.bank.simulation.Simulator;
import com.bank.ui.pages.SimulationPanel;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class SimulationPanelController {
    private final SimulationPanel view;
    private final Simulator simulator;
    private Map<String, JTextField> simulationParamFields;

    public SimulationPanelController(SimulationPanel view) {
        this.view = view;
        this.simulator = new Simulator();

        loadParams();
        setupActions();
    }

    private void loadParams() {
        simulationParamFields = view.addSimulationParameter(Map.of(
                "simulation_days", "Simulation Days",
                "simulation_customers", "Customers per Day",
                "simulation_repetition", "Simulation Repetition"
        ));
    }

    private void startSimulation() {
        simulator.setSimulationCustomers(Integer.parseInt(simulationParamFields.get("simulation_customers").getText()));
        simulator.setSimulationDays(Integer.parseInt(simulationParamFields.get("simulation_days").getText()));
        simulator.setSimulationRetries(Integer.parseInt(simulationParamFields.get("simulation_repetition").getText()));

        simulator.startSimulation(() -> showSuccessMessage("Simulation Complete"));
    }

    private void setupActions() {
        view.setStartButtonAction(action -> startSimulation());
    }

    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(view, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
