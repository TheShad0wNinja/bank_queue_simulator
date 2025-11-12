package com.bank.controllers;

import com.bank.models.Employee;
import com.bank.ui.pages.SettingsPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsPanelController {
    private final SettingsPanel view;
    private final Parameters parameters;

    public SettingsPanelController(SettingsPanel view) {
        this.view = view;
        this.parameters = Parameters.instance;

        setupActions();
        loadParams();
    }

    public void loadParams() {
        view.setGeneralConfigField("outdoorQueueSize", String.valueOf(parameters.getOutdoorQueueCapacity()));
        view.setGeneralConfigField("cashCustomerProp", String.valueOf(parameters.getCashCustomerProbability()));
        view.setGeneralConfigField("numOutdoorTellers", String.valueOf(parameters.getOutdoorCashEmployees().size()));
        view.setGeneralConfigField("numIndoorTellers", String.valueOf(parameters.getIndoorCashEmployees().size()));
        view.setGeneralConfigField("numIndoorServiceEmp", String.valueOf(parameters.getIndoorServiceEmployees().size()));


        view.clearTables();

        view.setTimeBetweenArrivalsTable(parameters.getTimeBetweenArrivalProbability());

        int outdoorTellerCount = 0;
        for (Employee employee : parameters.getOutdoorCashEmployees()) {
            view.addEmployeeTable("outdoor_teller_" + outdoorTellerCount++, employee);
        }

        int indoorTellerCount = 0;
        for (Employee employee : parameters.getIndoorCashEmployees()) {
            view.addEmployeeTable("indoor_teller_" + indoorTellerCount++, employee);
        }

        int indoorServiceCount = 0;
        for (Employee employee : parameters.getIndoorServiceEmployees()) {
            view.addEmployeeTable("indoor_service_" + indoorServiceCount++, employee);
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

            parameters.setOutdoorQueueCapacity(outdoorQueueSize);
            parameters.setCashCustomerProbability(cashCustomerProp);

            parameters.setTimeBetweenArrivalProbability(extractProbabilitiesFromTable(view.getTimeBetweenArrivalsTable().getTableData()));

            List<Employee> newEmployees = new ArrayList<>();

            var tables = view.getAllEmployeeTables();

            for (int i = 0; i < numOutdoorTellers; i++) {
                String key = "outdoor_teller_" + i;

                Map<Integer, Double> serviceTimes = new HashMap<>(Map.of(0, 1.0));
                if (tables.containsKey(key)) {
                    serviceTimes = extractProbabilitiesFromTable(tables.get(key).getTableData());
                }

                newEmployees.add(new Employee(
                        Employee.Area.OUTDOOR,
                        Employee.Type.CASH,
                        serviceTimes
                ));
            }

            for (int i = 0; i < numIndoorTellers; i++) {
                String key = "indoor_teller_" + i;

                Map<Integer, Double> serviceTimes = new HashMap<>(Map.of(0, 1.0));
                if (tables.containsKey(key)) {
                    serviceTimes = extractProbabilitiesFromTable(tables.get(key).getTableData());
                }

                newEmployees.add(new Employee(
                        Employee.Area.INDOOR,
                        Employee.Type.CASH,
                        serviceTimes
                ));
            }

            for (int i = 0; i < numIndoorServiceEmp; i++) {
                String key = "indoor_service_" + i;

                Map<Integer, Double> serviceTimes = new HashMap<>(Map.of(0, 1.0));
                if (tables.containsKey(key)) {
                    serviceTimes = extractProbabilitiesFromTable(tables.get(key).getTableData());
                }

                newEmployees.add(new Employee(
                        Employee.Area.INDOOR,
                        Employee.Type.SERVICE,
                        serviceTimes
                ));
            }

            parameters.setEmployees(newEmployees);

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
            parameters.resetParamsToDefault();
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