package com.bank.controllers;

import com.bank.models.EmployeeData;
import com.bank.simulation.SimulationConfigs;
import com.bank.ui.pages.SettingsPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bank.models.ServiceType.CASH;
import static com.bank.models.ServiceType.SERVICE;

public class SettingsPanelController {
    private final SettingsPanel view;
    private final SimulationConfigs simulationConfigs;

    public SettingsPanelController(SettingsPanel view) {
        this.view = view;
        this.simulationConfigs = SimulationConfigs.instance;

        setupActions();
        loadParams();
    }

    public void loadParams() {
        view.setGeneralConfigField("outdoorQueueSize", String.valueOf(simulationConfigs.getOutdoorQueueCapacity()));
        view.setGeneralConfigField("cashCustomerProp", String.valueOf(simulationConfigs.getCashCustomerProbability()));
        view.setGeneralConfigField("numOutdoorTellers", String.valueOf(simulationConfigs.getOutdoorCashEmployees().size()));
        view.setGeneralConfigField("numIndoorTellers", String.valueOf(simulationConfigs.getIndoorCashEmployees().size()));
        view.setGeneralConfigField("numIndoorServiceEmp", String.valueOf(simulationConfigs.getIndoorServiceEmployees().size()));


        view.clearTables();

        view.setTimeBetweenArrivalsTable(simulationConfigs.getTimeBetweenArrivalProbability());

        int outdoorTellerCount = 0;
        for (EmployeeData employeeData : simulationConfigs.getOutdoorCashEmployees()) {
            view.addEmployeeTable("outdoor_teller_" + outdoorTellerCount++, employeeData);
        }

        int indoorTellerCount = 0;
        for (EmployeeData employeeData : simulationConfigs.getIndoorCashEmployees()) {
            view.addEmployeeTable("indoor_teller_" + indoorTellerCount++, employeeData);
        }

        int indoorServiceCount = 0;
        for (EmployeeData employeeData : simulationConfigs.getIndoorServiceEmployees()) {
            view.addEmployeeTable("indoor_service_" + indoorServiceCount++, employeeData);
        }
    }

    public void saveParams() {
        try {
            int outdoorQueueSize = getIntValue("outdoorQueueSize");
            int numOutdoorTellers = getIntValue("numOutdoorTellers");
            int numIndoorTellers = getIntValue("numIndoorTellers");
            int numIndoorServiceEmp = getIntValue("numIndoorServiceEmp");
            double cashCustomerProp = getDoubleValue("cashCustomerProp");

            if (outdoorQueueSize < 0 || numOutdoorTellers < 0 ||
                    numIndoorTellers < 0 || numIndoorServiceEmp < 0 || cashCustomerProp < 0) {
                showError("All values must be non-negative numbers");
                return;
            }

            simulationConfigs.setOutdoorQueueCapacity(outdoorQueueSize);
            simulationConfigs.setCashCustomerProbability(cashCustomerProp);

            simulationConfigs.setTimeBetweenArrivalProbability(extractProbabilitiesFromTable(view.getTimeBetweenArrivalsTable().getTableData()));

            List<EmployeeData> newEmployeeData = new ArrayList<>();

            var tables = view.getAllEmployeeTables();

            for (int i = 0; i < numOutdoorTellers; i++) {
                String key = "outdoor_teller_" + i;

                Map<Integer, Double> serviceTimes = new HashMap<>(Map.of(0, 1.0));
                if (tables.containsKey(key)) {
                    serviceTimes = extractProbabilitiesFromTable(tables.get(key).getTableData());
                }

                newEmployeeData.add(new EmployeeData(
                        EmployeeData.Area.OUTDOOR,
                        CASH,
                        serviceTimes
                ));
            }

            for (int i = 0; i < numIndoorTellers; i++) {
                String key = "indoor_teller_" + i;

                Map<Integer, Double> serviceTimes = new HashMap<>(Map.of(0, 1.0));
                if (tables.containsKey(key)) {
                    serviceTimes = extractProbabilitiesFromTable(tables.get(key).getTableData());
                }

                newEmployeeData.add(new EmployeeData(
                        EmployeeData.Area.INDOOR,
                        CASH,
                        serviceTimes
                ));
            }

            for (int i = 0; i < numIndoorServiceEmp; i++) {
                String key = "indoor_service_" + i;

                Map<Integer, Double> serviceTimes = new HashMap<>(Map.of(0, 1.0));
                if (tables.containsKey(key)) {
                    serviceTimes = extractProbabilitiesFromTable(tables.get(key).getTableData());
                }

                newEmployeeData.add(new EmployeeData(
                        EmployeeData.Area.INDOOR,
                        SERVICE,
                        serviceTimes
                ));
            }

            simulationConfigs.setEmployees(newEmployeeData);

            showSuccess("Settings saved successfully!");

            loadParams();
        } catch (NumberFormatException e) {
            showError("Please enter valid numbers in all fields");
        } catch (Exception e) {
            showError("Error saving settings: " + e.getMessage());
        }
    }

    public void resetParams() {
        int confirm = JOptionPane.showConfirmDialog(view, "Are you sure you want to reset all settings to default?", "Confirm Reset", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            simulationConfigs.resetParamsToDefault();
            loadParams();
            showSuccess("Settings reset to defaults");
        }
    }

    private void setupActions() {
        view.setSaveButtonAction(e -> saveParams());
        view.setResetButtonAction(e -> resetParams());
    }

    private Map<Integer, Double> extractProbabilitiesFromTable(Object[][] tableData) {
        Map<Integer, Double> probabilities = new HashMap<>();

        for (Object[] row : tableData) {
            if (row[0] == null || row[1] == null) continue;

            try {
                int value = Integer.parseInt(row[0].toString());
                double probability = Double.parseDouble(row[1].toString());

                probabilities.put(value, probability);
            } catch (NumberFormatException ignored) {
            }
        }

        return probabilities;
    }

    private double getDoubleValue(String key) throws NumberFormatException {
        String value = view.getGeneralConfigField(key);
        if (value == null || value.trim().isEmpty()) return 0.0;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private int getIntValue(String key) throws NumberFormatException {
        String value = view.getGeneralConfigField(key);
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(value.trim());
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(view, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(view, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}