package com.bank.controllers;

import com.bank.models.Employee;
import com.bank.models.Range;
import com.bank.ui.panels.SettingsPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class SettingsPanelController {
    private final SettingsPanel view;
    private final Parameters parameters;

    public SettingsPanelController(SettingsPanel view) {
        this.view = view;
        this.parameters = Parameters.instance;

        // Setup button actions first
        setupActions();

        // Then load parameters
        loadParams();
    }

    /**
     * Load parameters from model to view
     */
    public void loadParams() {
        // Load general configuration
        view.setGeneralConfigField("outdoorQueueSize",
                String.valueOf(parameters.getOutdoorQueueCapacity()));
        view.setGeneralConfigField("numOutdoorTellers",
                String.valueOf(parameters.getOutdoorCashEmployees().size()));
        view.setGeneralConfigField("numIndoorTellers",
                String.valueOf(parameters.getIndoorCashEmployees().size()));
        view.setGeneralConfigField("numIndoorServiceEmp",
                String.valueOf(parameters.getIndoorServiceEmployees().size()));

        // Clear existing employee tables
        view.clearEmployeeTables();

        // Load employee tables
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

    /**
     * Save parameters from view to model
     */
    public void saveParams() {
        try {
            // Validate and save general configs
            int outdoorQueueSize = getIntValue("outdoorQueueSize");
            int numOutdoorTellers = getIntValue("numOutdoorTellers");
            int numIndoorTellers = getIntValue("numIndoorTellers");
            int numIndoorServiceEmp = getIntValue("numIndoorServiceEmp");

            // Validate values
            if (outdoorQueueSize < 0 || numOutdoorTellers < 0 ||
                    numIndoorTellers < 0 || numIndoorServiceEmp < 0) {
                showError("All values must be non-negative numbers");
                return;
            }

            // Update parameters
            parameters.setOutdoorQueueCapacity(outdoorQueueSize);

            // Rebuild employee list based on counts and table data
            List<Employee> newEmployees = new ArrayList<>();

            // Get service time probabilities from tables and create employees
            var tables = view.getAllEmployeeTables();

            // Outdoor tellers
            for (int i = 0; i < numOutdoorTellers; i++) {
                String key = "outdoor_teller_" + i;
                ArrayList<Range> serviceTimeRanges = getDefaultCashServiceTimeRanges();

                if (tables.containsKey(key)) {
                    serviceTimeRanges = extractRangesFromTable(tables.get(key).getTableData());
                }

                newEmployees.add(new Employee(
                        Employee.Area.OUTDOOR,
                        Employee.Type.CASH,
                        serviceTimeRanges
                ));
            }

            // Indoor tellers
            for (int i = 0; i < numIndoorTellers; i++) {
                String key = "indoor_teller_" + i;
                ArrayList<Range> serviceTimeRanges = getDefaultCashServiceTimeRanges();

                if (tables.containsKey(key)) {
                    serviceTimeRanges = extractRangesFromTable(tables.get(key).getTableData());
                }

                newEmployees.add(new Employee(
                        Employee.Area.INDOOR,
                        Employee.Type.CASH,
                        serviceTimeRanges
                ));
            }

            // Indoor service employees
            for (int i = 0; i < numIndoorServiceEmp; i++) {
                String key = "indoor_service_" + i;
                ArrayList<Range> serviceTimeRanges = getDefaultServiceTimeRanges();

                if (tables.containsKey(key)) {
                    serviceTimeRanges = extractRangesFromTable(tables.get(key).getTableData());
                }

                newEmployees.add(new Employee(
                        Employee.Area.INDOOR,
                        Employee.Type.SERVICE,
                        serviceTimeRanges
                ));
            }

            // Validate all probability distributions
            for (Employee employee : newEmployees) {
                if (!validateProbabilityDistribution(employee.getServiceTimeProbabilities())) {
                    showError("Invalid probability distribution for one or more employees. " +
                            "Check that probabilities are continuous and sum to 1.0");
                    return;
                }
            }

            // Save to parameters
            parameters.setEmployees(newEmployees);

            showSuccess("Settings saved successfully!");

        } catch (NumberFormatException e) {
            showError("Please enter valid numbers in all fields");
        } catch (Exception e) {
            showError("Error saving settings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Reset to default parameters
     */
    public void resetParams() {
        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Are you sure you want to reset all settings to default?",
                "Confirm Reset",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            parameters.resetParamsToDefault();
            loadParams();
            showSuccess("Settings reset to defaults");
        }
    }

    /**
     * Setup button actions
     */
    private void setupActions() {
        view.setSaveButtonAction(e -> saveParams());
        view.setResetButtonAction(e -> resetParams());
    }

    /**
     * Extract ranges from table data
     */
    private ArrayList<Range> extractRangesFromTable(Object[][] tableData) {
        ArrayList<Range> ranges = new ArrayList<>();
        double cumulativeProb = 0.0;

        for (Object[] row : tableData) {
            if (row[0] == null || row[1] == null) continue;

            try {
                int value = Integer.parseInt(row[0].toString());
                double probability = Double.parseDouble(row[1].toString());

                double min = cumulativeProb;
                double max = cumulativeProb + probability;

                ranges.add(new Range(min, max, value));
                cumulativeProb = max;
            } catch (NumberFormatException e) {
                // Skip invalid rows
            }
        }

        return ranges;
    }

    /**
     * Get default cash service time ranges
     */
    private ArrayList<Range> getDefaultCashServiceTimeRanges() {
        return new ArrayList<>(List.of(
                new Range(0.1, 0.20, 2),
                new Range(0.21, 0.50, 3),
                new Range(0.51, 1, 4)
        ));
    }

    /**
     * Get default service time ranges
     */
    private ArrayList<Range> getDefaultServiceTimeRanges() {
        return new ArrayList<>(List.of(
                new Range(0.1, 0.20, 4),
                new Range(0.21, 0.70, 6),
                new Range(0.71, 1, 8)
        ));
    }

    /**
     * Validate probability distribution
     */
    private boolean validateProbabilityDistribution(ArrayList<Range> ranges) {
        if (ranges.isEmpty()) return false;

        double totalProbability = 0.0;
        double lastMax = 0.0;

        for (Range range : ranges) {
            // Check continuity
            if (Math.abs(range.low() - lastMax) > 0.0001) {
                return false;
            }

            totalProbability += (range.high() - range.low());
            lastMax = range.high();
        }

        // Check if total probability is approximately 1.0
        return Math.abs(totalProbability - 1.0) < 0.0001;
    }

    /**
     * Get integer value from view
     */
    private int getIntValue(String key) throws NumberFormatException {
        String value = view.getGeneralConfigField(key);
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(value.trim());
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(
                view,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Show success message
     */
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(
                view,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}