package com.bank.simulation;

public class Simulator {
    private final SimulationConfigs simulationConfigs;

    private int simulationDays = 10;
    private int simulationCustomers = 10;
    private int simulationRetries = 10;

    public Simulator() {
        simulationConfigs = SimulationConfigs.instance;
    }

    public int getSimulationRetries() {
        return simulationRetries;
    }

    public void setSimulationRetries(int simulationRetries) {
        this.simulationRetries = simulationRetries;
    }

    public int getSimulationCustomers() {
        return simulationCustomers;
    }

    public void setSimulationCustomers(int simulationCustomers) {
        this.simulationCustomers = simulationCustomers;
    }

    public int getSimulationDays() {
        return simulationDays;
    }

    public void setSimulationDays(int simulationDays) {
        this.simulationDays = simulationDays;
    }
}
