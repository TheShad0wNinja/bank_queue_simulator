package com.bank.controllers;

import com.bank.ui.panels.SettingsPanel;

public class SettingsPanelController {
    private final SettingsPanel view;
    private final Parameters parameters;

    public SettingsPanelController(SettingsPanel view) {
        this.view = view;
        this.parameters = Parameters.instance;

        loadParams();
    }

    public void loadParams() {
        view.setGeneralConfigField("outdoorQueueSize", String.valueOf(parameters.getOutdoorQueueCapacity()));
        view.setGeneralConfigField("numOutdoorTellers", String.valueOf(parameters.getOutdoorCashEmployees().size()));
        view.setGeneralConfigField("numIndoorTellers", String.valueOf(parameters.getIndoorCashEmployees().size()));
        view.setGeneralConfigField("numIndoorServiceEmp", String.valueOf(parameters.getIndoorServiceEmployees().size()));

        parameters.getOutdoorCashEmployees().forEach(view::addEmployee);
        parameters.getIndoorCashEmployees().forEach(view::addEmployee);
        parameters.getIndoorServiceEmployees().forEach(view::addEmployee);
    }
}
