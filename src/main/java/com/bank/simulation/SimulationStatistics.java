package com.bank.simulation;

public class SimulationStatistics {
    private int totalCashServiceTime = 0;
    private int totalServiceServiceTime = 0;

    private int totalIndoorTellerWaitTime = 0;
    private int totalOutdoorTellerWaitTime = 0;
    private int totalServiceEmployeeWaitTime = 0;

    private int maxIndoorTellerQueueSize = 0;
    private int maxOutdoorTellerQueueSize = 0;
    private int maxServiceEmployeeQueueSize = 0;

    private int totalIndoorTellerWaitingCustomers = 0;
    private int totalOutdoorTellerWaitingCustomers = 0;
    private int totalServiceEmployeeWaitingCustomers = 0;

    private int totalIndoorTellerCustomers = 0;
    private int totalOutdoorTellerCustomers = 0;
    private int totalServiceEmployeeCustomers = 0;

    private int totalTime = 0;
    private int totalIndoorTellerIdleTime = 0;
    private int totalOutdoorTellerIdleTime = 0;
    private int totalServiceEmployeeIdleTime = 0;

    public void addTotalCashServiceTime(int time) {
        totalCashServiceTime += time;
    }

    public void addTotalServiceServiceTime(int time) {
        totalServiceServiceTime += time;
    }

    public void addTotalIndoorTellerWaitTime(int time) {
        totalIndoorTellerWaitTime += time;
    }

    public void addTotalOutdoorTellerWaitTime(int time) {
        totalOutdoorTellerWaitTime += time;
    }

    public void addTotalServiceEmployeeWaitTime(int time) {
        totalServiceEmployeeWaitTime += time;
    }

    public void updateIndoorTellerMaxQueueSize(int size) {
        maxIndoorTellerQueueSize = Math.max(maxIndoorTellerQueueSize, size);
    }

    public void updateOutdoorTellerMaxQueueSize(int size) {
        maxOutdoorTellerQueueSize = Math.max(maxOutdoorTellerQueueSize, size);
    }

    public void updateServiceEmployeeMaxQueueSize(int size) {
        maxServiceEmployeeQueueSize = Math.max(maxServiceEmployeeQueueSize, size);
    }

    public void addIndoorTellerCustomer() {
        totalIndoorTellerCustomers += 1;
    }

    public void addOutdoorTellerCustomer() {
        totalOutdoorTellerCustomers += 1;
    }

    public void addServiceEmployeeCustomer() {
        totalServiceEmployeeCustomers += 1;
    }

    public void addIndoorTellerWaitingCustomer() {
        totalIndoorTellerWaitingCustomers += 1;
    }

    public void addOutdoorTellerWaitingCustomer() {
        totalOutdoorTellerWaitingCustomers += 1;
    }

    public void addServiceEmployeeWaitingCustomer() {
        totalServiceEmployeeWaitingCustomers += 1;
    }

    public void setTotalIndoorTellerIdleTime(int time) {
        totalIndoorTellerIdleTime = time;
    }

    public void setTotalServiceEmployeeIdleTime(int time) {
        this.totalServiceEmployeeIdleTime = time;
    }

    public void setTotalOutdoorTellerIdleTime(int time) {
        this.totalOutdoorTellerIdleTime = time;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public void addSimulationStatistics(SimulationStatistics stats) {
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String format = "| %-40s | %-10s |%n";

        sb.append("+------------------------------------------+------------+\n");
        sb.append(String.format(format, "Metric", "Value"));
        sb.append("+------------------------------------------+------------+\n");
        sb.append(String.format(format, "totalCashServiceTime", totalCashServiceTime));
        sb.append(String.format(format, "totalServiceServiceTime", totalServiceServiceTime));
        sb.append(String.format(format, "totalIndoorTellerWaitTime", totalIndoorTellerWaitTime));
        sb.append(String.format(format, "totalOutdoorTellerWaitTime", totalOutdoorTellerWaitTime));
        sb.append(String.format(format, "totalServiceEmployeeWaitTime", totalServiceEmployeeWaitTime));
        sb.append(String.format(format, "maxIndoorTellerQueueSize", maxIndoorTellerQueueSize));
        sb.append(String.format(format, "maxOutdoorTellerQueueSize", maxOutdoorTellerQueueSize));
        sb.append(String.format(format, "maxServiceEmployeeQueueSize", maxServiceEmployeeQueueSize));
        sb.append(String.format(format, "totalIndoorTellerWaitingCustomers", totalIndoorTellerWaitingCustomers));
        sb.append(String.format(format, "totalOutdoorTellerWaitingCustomers", totalOutdoorTellerWaitingCustomers));
        sb.append(String.format(format, "totalServiceEmployeeWaitingCustomers", totalServiceEmployeeWaitingCustomers));
        sb.append(String.format(format, "totalIndoorTellerCustomers", totalIndoorTellerCustomers));
        sb.append(String.format(format, "totalOutdoorTellerCustomers", totalOutdoorTellerCustomers));
        sb.append(String.format(format, "totalServiceEmployeeCustomers", totalServiceEmployeeCustomers));
        sb.append(String.format(format, "totalTime", totalTime));
        sb.append(String.format(format, "totalIndoorTellerIdleTime", totalIndoorTellerIdleTime));
        sb.append(String.format(format, "totalOutdoorTellerIdleTime", totalOutdoorTellerIdleTime));
        sb.append(String.format(format, "totalServiceEmployeeIdleTime", totalServiceEmployeeIdleTime));
        sb.append("+------------------------------------------+------------+\n");

        return sb.toString();
    }}
